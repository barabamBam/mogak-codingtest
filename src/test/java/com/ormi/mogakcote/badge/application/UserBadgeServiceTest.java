package com.ormi.mogakcote.badge.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.domain.Badge;
import com.ormi.mogakcote.badge.domain.UserBadge;
import com.ormi.mogakcote.badge.infrastructure.BadgeRepository;
import com.ormi.mogakcote.badge.infrastructure.UserBadgeRepository;
import com.ormi.mogakcote.user.domain.Activity;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserBadgeServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserBadgeRepository userBadgeRepository;

	@Mock
	private BadgeRepository badgeRepository;

	@InjectMocks
	private UserBadgeService userBadgeService;

  @Test
  void makeUserBadge1() {
	  User registeredUser;
	  Activity activity = new Activity();
	  int i=0;
	  while(i < 49) {
	  	activity.increaseCommentCount();
	  	i++;
	  }
	  registeredUser = User.builder().id(1L).activity(activity).build();

	  UserBadge userBadge = UserBadge.builder().id(1L).userId(1L).badgeId(1L).build();
	  Badge badge = Badge.builder().id(1L).name("COMMENT_20").build();

	  List<UserBadge> userBadgeList = new ArrayList<>();
	  userBadgeList.add(userBadge);

	  when(userRepository.findCommentCountById(1L))
		  .thenReturn(registeredUser.getActivity().getCommentCount());

	  when(badgeRepository.findByName("COMMENT_20"))
		  .thenReturn(Optional.ofNullable(badge));

	  when(userBadgeRepository.findAllByUserId(1L))
		  .thenReturn(userBadgeList);

	  AuthUser user = new AuthUser(1L);
	  userBadgeService.makeUserBadge(user,"COMMENT");
  }

	@Test
	void makeUserBadge2() {
		User registeredUser;
		Activity activity = new Activity();
		int i=0;
		while(i < 51) {
			activity.increaseCommentCount();
			i++;
		}
		registeredUser = User.builder().id(1L).activity(activity).build();

		UserBadge userBadge = UserBadge.builder().id(1L).userId(1L).badgeId(1L).build();
		Badge badge2 = Badge.builder().id(2L).name("COMMENT_50").build();

		List<UserBadge> userBadgeList = new ArrayList<>();
		userBadgeList.add(userBadge);

		when(userRepository.findCommentCountById(1L))
			.thenReturn(registeredUser.getActivity().getCommentCount());

		when(badgeRepository.findByName("COMMENT_50"))
			.thenReturn(Optional.ofNullable(badge2));

		when(userBadgeRepository.findAllByUserId(1L))
			.thenReturn(userBadgeList);

		AuthUser user = new AuthUser(1L);
		userBadgeService.makeUserBadge(user,"COMMENT");
	}
}