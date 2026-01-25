package com.rpgmanager.backend.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class SessionTest {

  @Test
  void testOnCreate() throws Exception {
    Session session = new Session();
    Method onCreate = Session.class.getDeclaredMethod("onCreate");
    onCreate.setAccessible(true);
    onCreate.invoke(session);
    assertThat(session.getSessionDate()).isNotNull();
  }

  @Test
  void testBuilderAndGetters() {
    OffsetDateTime now = OffsetDateTime.now();
    Session session =
        Session.builder()
            .id(1L)
            .name("Name")
            .description("Desc")
            .sessionDate(now)
            .status(Session.SessionStatus.ACTIVE)
            .build();

    assertThat(session.getId()).isEqualTo(1L);
    assertThat(session.getName()).isEqualTo("Name");
    assertThat(session.getDescription()).isEqualTo("Desc");
    assertThat(session.getSessionDate()).isEqualTo(now);
    assertThat(session.getStatus()).isEqualTo(Session.SessionStatus.ACTIVE);
  }

  @Test
  void testNoArgsConstructor() {
    Session session = new Session();
    session.setName("Test");
    assertThat(session.getName()).isEqualTo("Test");
  }
}
