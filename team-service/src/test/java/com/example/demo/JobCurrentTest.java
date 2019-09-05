package com.example.demo;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.example.wfm.bean.JobCurrent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JobCurrentTest {

    @Test
    public void testUnMarshal() throws JAXBException {
        File file = new File("test-job-current.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(JobCurrent.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JobCurrent result = (JobCurrent) jaxbUnmarshaller.unmarshal(file);
        System.out.println(result);
    }
}
