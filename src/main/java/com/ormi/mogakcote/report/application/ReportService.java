package com.ormi.mogakcote.report.application;

import static com.ormi.mogakcote.report.FastAPIConstants.CLIENT_ID;
import static com.ormi.mogakcote.report.FastAPIConstants.FAST_API_URL;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.problem.ProblemInvalidException;
import com.ormi.mogakcote.exception.report.ReportInvalidException;
import com.ormi.mogakcote.problem.domain.Algorithm;
import com.ormi.mogakcote.problem.domain.Language;
import com.ormi.mogakcote.problem.domain.Platform;
import com.ormi.mogakcote.problem.domain.ProblemReportAlgorithm;
import com.ormi.mogakcote.problem.infrastructure.AlgorithmRepository;
import com.ormi.mogakcote.problem.infrastructure.LanguageRepository;
import com.ormi.mogakcote.problem.infrastructure.PlatformRepository;
import com.ormi.mogakcote.problem.infrastructure.ProblemReportAlgorithmRepository;
import com.ormi.mogakcote.report.domain.CodeReport;
import com.ormi.mogakcote.report.domain.ProblemReport;
import com.ormi.mogakcote.report.dto.request.ReportRequest;
import com.ormi.mogakcote.report.dto.response.ApiResponse;
import com.ormi.mogakcote.report.dto.response.ReportResponse;
import com.ormi.mogakcote.report.infrastructure.CodeReportRepository;
import com.ormi.mogakcote.report.infrastructure.ProblemReportRepository;
import com.ormi.mogakcote.report.util.CodeReportContent;
import com.ormi.mogakcote.report.util.ProblemReportContent;
import com.ormi.mogakcote.report.util.ReportContentParser;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProblemReportRepository problemReportRepository;
    private final AlgorithmRepository algorithmRepository;
    private final LanguageRepository languageRepository;
    private final PlatformRepository platformRepository;
    private final ProblemReportAlgorithmRepository problemReportAlgorithmRepository;
    private final ReportContentParser reportContentParser;
    private final RestTemplate restTemplate;
    private final CodeReportRepository codeReportRepository;

    /**
     * 보고서 생성
     */
    @Transactional
    public ReportResponse createReport(AuthUser user, ReportRequest request) {
        // TODO 처리율 제한기

        log.info("user id = {}", user.getId());

        Platform platform = getPlatformOrThrowIfNotExist(
                request.getPlatformId());
        Language language = getLanguageOrThrowIfNotExist(
                request.getLanguageId());

        // 해당 게시글에 대한 코드 분석이 기존에 있는지 찾는다.
        CodeReport codeReport = codeReportRepository.findByPostId(request.getPostId());
        if (codeReport == null) {

            // 코드 보고서 응답 받기
            String codeContent = getApiResponse(request, platform, language, true);

            // 응답 파싱
            CodeReportContent codeReportContent = reportContentParser.parseAnswerForCode(
                    codeContent);

            // 코드 보고서 저장
            codeReport = buildAndSaveCodeReport(codeReportContent,
                    request);
        }

        // 플랫폼, 문제 번호, 사용 언어에 대해 동일한 문제 분석이 기존에 있는지 찾는다.
        ProblemReport problemReport = problemReportRepository.findByPlatformIdAndProblemNumberAndLanguageId(
                platform.getId(),
                request.getProblemNumber(),
                language.getId());

        if (problemReport == null) {

            // 문제 보고서 응답 받기
            String problemContent = getApiResponse(request, platform, language, false);

            // 응답 파싱
            ProblemReportContent problemReportContent = reportContentParser.parseAnswerForProblem(
                    problemContent);

            problemReport = buildAndSaveProblemReport(request,
                    problemReportContent, platform,
                    language);

            // 문제 해결 알고리즘 저장
            buildAndSaveProblemReportAlgorithms(problemReportContent, problemReport);
        }

        return ReportResponse.toResponse(
                codeReport.getId(),
                codeReport.getFullReport(),
                problemReport.getId(),
                problemReport.getFullReport());
    }


    private Platform getPlatformOrThrowIfNotExist(Long platformId) {
        return platformRepository.findById(platformId).orElseThrow(
                () -> new ProblemInvalidException(ErrorType.PLATFORM_NOT_FOUND_ERROR)
        );
    }

    private Language getLanguageOrThrowIfNotExist(Long languageId) {
        return languageRepository.findById(languageId).orElseThrow(
                () -> new ProblemInvalidException(ErrorType.LANGUAGE_NOT_FOUND_ERROR)
        );
    }

    private String getApiResponse(
            ReportRequest request, Platform platform, Language language,
            boolean isCodeQuestionRequested
    ) {
        String question;

        String algorithms = formatAlgorithmNames(algorithmRepository.findAll());

        if (isCodeQuestionRequested) {
            question = generateCodeQuestion(
                    platform.getName(),
                    request.getProblemNumber(),
                    language.getName(),
                    request.getCode());
        } else {
            question = generateProblemQuestion(
                    platform.getName(),
                    request.getProblemNumber(),
                    language.getName(),
                    algorithms
            );

        }

        log.info("*** isCodeQuestionRequested = " + isCodeQuestionRequested);
        log.info("*** question = \n" + question);

        // 앨런 검색 요청
        try {
            ResponseEntity<ApiResponse> response = getResponseFromFastAPI(question);

            // 앨런 검색 결과 해당 문제가 존재하지 않을 경우 예외처리
            throwIfProblemNotExist(response);
            return response.getBody().getContent();
        } catch (HttpServerErrorException e) {
            log.error("FastAPI server error: ", e);
            throw new ReportInvalidException(ErrorType.FAST_API_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Unexpected error while calling FastAPI: ", e);
            throw new ReportInvalidException(ErrorType.FAST_API_CALL_ERROR);
        }

    }

    private static String formatAlgorithmNames(List<Algorithm> algorithms) {
        return algorithms.stream()
                .map(Algorithm::getName)
                .collect(Collectors.joining(", "));
    }

    // 코드 분석 요청 내용 생성
    private String generateCodeQuestion(
            String platformName, int problemNumber, String language, String code) {
        return String.format(
                "코드 분석 요청이야.\n %s 의 %d 번 문제에 대해 %s 언어로 작성한 아래 <코드> 에 대해, 정확하게 다음 <항목>에 대해서 <조건>을 만족하는 형식으로 알려줘.\n\n"
                        +
                        "<항목>\n'성공/실패 여부', '코드 개선점 제안'\n\n" +
                        "<조건>\n여기서 성공/실패 여부는 성공이면 true, 실패면 false 로 알려줘. 또 답변은 오직 내가 요구한 항목에 대해서만 '[항목] 답변 <END>' 의 일관된 형식으로 주고, 항목을 쓸 때를 제외하면 답변에 '[', ']' 는 포함되지 않도록 해줘. "
                        +
                        "만약 해당 문제가 존재하지 않으면 다른 문자 하나 없이, 정확히 'Not Found' 라고만 답변해줘.\n\n" +
                        "<코드>\n%s",
                platformName,
                problemNumber,
                language,
                code);
    }

    // 문제 분석 요청 내용 생성
    private String generateProblemQuestion(
            String platformName, int problemNumber, String language, String algorithms) {
        return String.format(
                "문제 분석 요청이야.\n %s 의 %d 번 문제에 대해 %s 언어로, 정확하게 다음 <항목>에 대해서 <조건>을 만족하는 형식으로 알려줘.\n\n"
                        +
                        "<항목>\n'해당 문제로 바로가는 url', '문제 분석', '풀이 접근', '해결하는 코드', '사용된 알고리즘', '해결 코드의 시간복잡도'\n\n"
                        +
                        "<조건>\n답변은 오직 내가 요구한 항목에 대해서만 '[항목] 답변 <END>' 의 일관된 형식으로 주고, 항목을 쓸 때를 제외하면 답변에 '[', ']' 는 포함되지 않도록 해줘. "
                        +
                        "또 사용된 알고리즘과 시간복잡도는 서술이 아닌 단답 형식으로 제시해줘. 사용된 알고리즘은 최대 3개 까지만 제시해주고, 여러 개라면 ', ' 으로 나눠줘."
                        +
                        "알고리즘은 내가 주는 다음 목록에서만 골라주고, 이 중에서 없으면 'X' 라고만 보내줘! \n\n"
                        +
                        "<알고리즘 목록>\n%s \n\n"
                        +
                        "만약 해당 문제가 존재하지 않으면 다른 문자 하나 없이, 정확히 'Not Found' 라고만 답변해줘. ",
                platformName,
                problemNumber,
                language,
                algorithms);
    }

    private ResponseEntity<ApiResponse> getResponseFromFastAPI(String requestQuestion) {
        String url = UriComponentsBuilder.fromHttpUrl(FAST_API_URL)
                .queryParam("content", requestQuestion)
                .queryParam("client_id", CLIENT_ID)
                .toUriString();
        return restTemplate.getForEntity(url, ApiResponse.class);
    }

    private void throwIfProblemNotExist(ResponseEntity<ApiResponse> response) {
        if (response.getBody() != null && response.getBody().getContent().equals("Not Found")) {
            throw new ProblemInvalidException(ErrorType.PROBLEM_NOT_FOUND_ERROR);
        }
    }

    private ProblemReport buildAndSaveProblemReport(
            ReportRequest request,
            ProblemReportContent content,
            Platform platform,
            Language language
    ) {
        ProblemReport problemReport = ProblemReport.builder()
                .source(content.getSource())
                .fullReport(content.getFullReport())
                .problemAnalysis(content.getProblemAnalysis())
                .problemApproach(content.getProblemApproach())
                .solutionCode(content.getSolutionCode())
                .timeComplexity(content.getTimeComplexity())
                .problemNumber(request.getProblemNumber())
                .platformId(platform.getId())
                .languageId(language.getId())
                .build();
        return problemReportRepository.save(problemReport);
    }

    private CodeReport buildAndSaveCodeReport(
            CodeReportContent content,
            ReportRequest request
    ) {
        CodeReport codeReport = CodeReport.builder()
                .isPassed(content.isPassed())
                .codeAnalysis(content.getCodeAnalysis())
                .fullReport(content.getFullReport())
                .postId(request.getPostId())
                .build();
        return codeReportRepository.save(codeReport);
    }

    private void buildAndSaveProblemReportAlgorithms(
            ProblemReportContent content,
            ProblemReport savedProblemReport
    ) {

        content.getSolutionAlgorithms().forEach(algorithmName -> {
            // 목록에 해당하는 알고리즘이 없을 경우 return
            // TODO 관리자에게 해당 알고리즘 추가 여부 검증?
//            if(algorithmName.equals("X")) return;
            Algorithm findAlgorithm = getAlgorithm(algorithmName);
            if(findAlgorithm == null) return;

            // 동일한 리포트와 알고리즘이 존재하지 않을 경우 저장
            if (!problemReportAlgorithmRepository.existsByAlgorithmIdAndProblemReportId(
                    findAlgorithm.getId(), savedProblemReport.getId())) {
                ProblemReportAlgorithm problemReportAlgorithm = ProblemReportAlgorithm.builder()
                        .algorithmId(findAlgorithm.getId())
                        .problemReportId(savedProblemReport.getId())
                        .build();
                problemReportAlgorithmRepository.save(problemReportAlgorithm);
            }
        });
    }

    private Algorithm getAlgorithm(String algorithmName) {
        return algorithmRepository.findByName(algorithmName);
    }

}
