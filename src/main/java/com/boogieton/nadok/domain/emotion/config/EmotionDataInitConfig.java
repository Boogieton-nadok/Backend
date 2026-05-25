package com.boogieton.nadok.domain.emotion.config;

import com.boogieton.nadok.domain.emotion.entity.Character;
import com.boogieton.nadok.domain.emotion.repository.CharacterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class EmotionDataInitConfig {

    @Bean
    public CommandLineRunner init(CharacterRepository characterRepository) {
        return args -> {
            // DB에 캐릭터 데이터가 없을 때만 초기 데이터를 세팅합니다.
            if (characterRepository.count() == 0) {

                Character character1 = Character.builder()
                        .characterName("어린 왕자")
                        .author("앙투안 드 생텍쥐페리")
                        .bookQuote("중요한 것은 눈에 보이지 않아. 마음으로 보아야 해.")
                        .characterImgUrl("http://3.37.177.20:8080/uploads/character4.png")
                        .characterThumbUrl("http://3.37.177.20:8080/uploads/character4_thumb.png")
                        .methodReason("뿌듯함과 지침이 동시에 느껴지는 오늘, 어린 왕자처럼 작은 것들의 소중함을 알면서도 그 무게에 지쳐있는 당신과 닮았어요.")
                        .keyword("슬픈, 공허한, 우울")
                        .build();

                Character character2 = Character.builder()
                        .characterName("앨리스")
                        .author("루이스 캐럴")
                        .bookQuote("이상하다는 건 나쁜 게 아니야. 최고로 좋은 사람들은 다 조금씩 이상하거든.")
                        .characterImgUrl("http://3.37.177.20:8080/uploads/character5.png")
                        .characterThumbUrl("http://3.37.177.20:8080/uploads/character5_thumb.png")
                        .methodReason("뭐가 맞는 건지 모르겠고 모든 게 낯설게 느껴지는 오늘, 앨리스도 처음엔 아무것도 이해하지 못한 채 그 세계에 뛰어들었어요. 괜찮아요, 원래 처음엔 다 그래요.")
                        .keyword("혼란, 피곤한, 신나는")
                        .build();

                Character character3 = Character.builder()
                        .characterName("앤 셜리")
                        .author("루시 모드 몽고메리")
                        .bookQuote("내일은 아직 아무 실수도 하지 않은 새로운 날이에요.")
                        .characterImgUrl("http://3.37.177.20:8080/uploads/character2.png")
                        .characterThumbUrl("http://3.37.177.20:8080/uploads/character2_thumb.png")
                        .methodReason("내 단점이 자꾸 눈에 밟히는 오늘, 앤은 누구보다 자신의 다름을 사랑하는 법을 알고 있어요. 있는 그대로의 나를 괜찮다고 말해줄 수 있는 캐릭터예요.")
                        .keyword("차분한, 뿌듯한")
                        .build();

                Character character4 = Character.builder()
                        .characterName("피터팬")
                        .author("제임스 매슈 배리")
                        .bookQuote("생각하면 날 수 있어. 행복한 생각 하나면 충분해.")
                        .characterImgUrl("http://3.37.177.20:8080/uploads/character3.png")
                        .characterThumbUrl("http://3.37.177.20:8080/uploads/character3_thumb.png")
                        .methodReason("책임과 현실이 무겁게 느껴지는 오늘, 피터팬은 어른이 되지 않아도 괜찮다고 말해줄 수 있는 유일한 캐릭터예요.")
                        .keyword("공허한, 스트레스, 우울")
                        .build();

                Character character5 = Character.builder()
                        .characterName("빨간 모자")
                        .author("그림 형제")
                        .bookQuote("길을 벗어난 건 실수였지만, 그 경험이 나를 더 단단하게 만들었어요.")
                        .characterImgUrl("http://3.37.177.20:8080/uploads/character1.png")
                        .characterThumbUrl("http://3.37.177.20:8080/uploads/character1_thumb.png")
                        .methodReason("내 직관을 무시하고 흔들렸거나, 누군가의 말에 경계를 잃은 날. 빨간 모자는 그 경험에서 가장 단단해지는 법을 알고 있어요.")
                        .keyword("분노, 스트레스, 피곤한")
                        .build();

                // List.of()를 사용해 리스트로 묶은 뒤 saveAll()로 한 번에 저장합니다.
                characterRepository.saveAll(List.of(
                        character1, character2, character3, character4, character5
                ));

                System.out.println("알림: 5개의 캐릭터 초기 데이터가 성공적으로 저장되었습니다!");
            }
        };
    }
}