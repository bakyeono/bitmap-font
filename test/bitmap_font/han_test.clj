(ns bitmap-font.han-test
  (:require [clojure.test :refer :all]
            [bitmap-font.han :refer :all]))

(deftest jamo-combination-test
  (testing "Testing Jamo combination fuctions ..."
    (is (= (jamo->full-han \ㄱ \ㅗ \ㄴ) \곤))
    (is (= (jamo->full-han \ㅎ \ㅔ \ㄹ) \헬))
    (is (= (jamo->full-han \ㅅ \ㅚ nil) \쇠))
    (is (= (jamo->full-han \ㅆ \ㅒ \ㄹ) \썔))
    (is (= (jamo->full-han \ㅋ \ㅜ \ㅅ) \쿳))
    (is (= (jamo->full-han \ㄹ \ㅡ \ㅇ) \릉))))

(deftest jamo-division-test
  (testing "Testing Jamo division fuctions ..."
    (is (= (full-han->jamo \곤) [\ㄱ \ㅗ \ㄴ]))
    (is (= (full-han->jamo \캥) [\ㅋ \ㅐ \ㅇ]))
    (is (= (full-han->jamo \알) [\ㅇ \ㅏ \ㄹ]))
    (is (= (full-han->jamo \쮀) [\ㅉ \ㅞ nil]))
    (is (= (full-han->jamo \흙) [\ㅎ \ㅡ \ㄺ]))
    (is (= (full-han->jamo \또) [\ㄸ \ㅗ nil]))))

(deftest qwerty->han2-test
  (testing "Testing qwerty -> han2 ..."
    (is (= (qwerty->han2 "dhsmfEkfk rlqnsdl ahqtl whgtmqslek.")
           "오늘따라 기분이 몹시 좋습니다."))
    (is (= (qwerty->han2 "dhoswl whgdms dlfdl todrlf rjt rkxtmqslek.")
           "왠지 좋은 일이 생길 것 같습니다."))
    (is (= (qwerty->han2 "WnjTqnpfqTnpfv")
           "쭸뷃쒪"))))

(deftest han2->qwerty-test
  (testing "Testing han2 -> qwerty ..."
    (is (= (han2->qwerty "마하반야바라밀다심경 관자재보살")
           "akgkqksdiqkfkalfektlarud rhkswkwoqhtkf"))
    (is (= (han2->qwerty "다람쥐 헌 쳇바퀴에 타고 파.")
           "ekfkawnl gjs cptqkznldp xkrh vk."))
    (is (= (han2->qwerty "쭸뷃쒪")
           "WnjTqnpfqTnpfv"))
    (is (= (han2->qwerty "키스의 고유 조건은 입술끼리 만나야 하고 특별한 요령은 필요치 않다.")
           "zltmdml rhdb whrjsdms dlqtnfRlfl aksskdi gkrh xmrqufgks dyfuddms vlfdycl dksgek."))))


