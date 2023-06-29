package com.wechat.wxpay.test;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class iotNetworkCardTest {

    /**
     * @description: 对原始敏感词txt进行修改，去除过长内容以及数字和英文
     * @author: 齐振浩
     * @date: 2023/2/28
     **/
    @Test
    void testHello() throws IOException {
        System.out.println("hello");

        List<String> badwords = new ArrayList<>();

        String path = "static/txt/CensorWords.txt";
        ClassPathResource resource = new ClassPathResource(path);
        InputStream inputStream = resource.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = br.readLine()) != null) {
            badwords.add(line.trim());
        }
        br.close();

        System.out.println(badwords);
        System.out.println("=====================================================================");
        System.out.println("=====================================================================");
        System.out.println("=====================================================================");

        for (int i = 0; i < badwords.size(); i++) {
            if (badwords.get(i).length() > 4) {
                badwords.remove(i);
                i--;
                continue;
            }
            //含有字母
            String regex1 = ".*[a-zA-z].*";
            boolean result3 = badwords.get(i).matches(regex1);
            //【含有数字】true
            String regex2 = ".*[0-9].*";
            boolean result4 = badwords.get(i).matches(regex2);
            if (result3 || result4) {
                badwords.remove(i);
                i--;
            }
        }
        System.out.println(badwords);

        //处理后的数据写出
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter("D:/newWord.txt"));
            for (int j = 0; j < badwords.size(); j++) {
                bw.write(badwords.get(j).toString());
                bw.newLine();
                bw.flush();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
