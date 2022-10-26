package fr.upem.concurrence.td04.exo3;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class VoteTest {

  @Test
  public void SimpleVote() throws Exception {
    var vote = new Vote(3);
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1_000);
        assertEquals("0", vote.vote("1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(500);
        assertEquals("0", vote.vote("0"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    assertEquals("0", vote.vote("0"));
  }

  @Test
  public void VoteAlone() throws InterruptedException {
    var vote = new Vote(1);
    assertEquals("0", vote.vote("0"));
  }




  @Test
  public void VoteWithATie() throws Exception {
    var vote = new Vote(2);
    Thread.ofPlatform().start(() -> {
      try {
        Thread.sleep(1_000);
        assertEquals("0", vote.vote("1"));
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    });
    assertEquals("0", vote.vote("0"));
  }

  @Test
  public void ManyVotes() throws Exception {
    var vote = new Vote(1);
    for(var i = 0; i < 4; i++) {
      Thread.ofPlatform().start(() -> {
        try {
          Thread.sleep(1_000);
          assertEquals("0", vote.vote("1"));
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
      });
    }
    assertEquals("0", vote.vote("0"));
  }
}
