package com.example.demo;

import com.example.demo.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserJpaTest {

    @Autowired
    private TestEntityManager tem;

    @Test
    public void mapping() {
        User v = this.tem.persistFlushFind(User.builder().password("blart").username("test").build());
//        assertThat(v.getUsername()).isEqualTo("test");
//        assertThat(v.getId()).isNotNull();
//        assertThat(v.getId()).isGreaterThan(0);
        //assertThat(v.getCreatedDate()).isNotNull();
    }
}
