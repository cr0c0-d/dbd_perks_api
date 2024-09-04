package dbd.perks.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")  // 환경변수에 저장된 jasypt.encryptor.password 값을 가져와서 사용할 것
    private String password;

    @Bean("jasyptStringEncryptor")  // Bean 이름 설정 - jasyptStringEncryptor
    public StringEncryptor stringEncryptor() {

        // PooledPBEStringEncryptor 클래스 : 비밀번호 기반 암호화(PBE)를 사용하여 문자열을 암/복호화
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();

        // SimpleStringPBEConfig 클래스 : 암호화 설정
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);   // 암호화 비밀번호
        config.setAlgorithm("PBEWithMD5AndTripleDES"); // 암호화 알고리즘
        config.setKeyObtentionIterations("1000");   // 키 생성 반복 횟수 설정
        config.setPoolSize("1");    // 인스턴스 풀 사이즈
        config.setProviderName("SunJCE");   // 암호화 서비스 제공자 설정
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");    // 솔트 생성기 설정 - org.jasypt.salt.RandomSaltGenerator 클래스
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");  // 초기화 벡터(IV) 생성기 - "org.jasypt.iv.RandomIvGenerator" 클래스
        config.setStringOutputType("base64");   // 암호화된 데이터 출력 형식 - base64
        encryptor.setConfig(config);    // encryptor에 설정 저장
        return encryptor;
    }
}
