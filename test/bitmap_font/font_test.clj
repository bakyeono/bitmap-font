(ns bitmap-font.font-test
  (:require [clojure.test :refer :all]
            [bitmap-font.font :refer :all]))

(deftest ch->sect-test
  (testing "Testing ch->sect function ..."
    (is (= (ch->sect \ㄱ) :hangul-jamo))
    (is (= (ch->sect \ㅏ) :hangul-jamo))
    (is (= (ch->sect \ㅎ) :hangul-jamo))
    (is (= (ch->sect \ㅝ) :hangul-jamo))
    (is (= (ch->sect \강) :hangul))
    (is (= (ch->sect \가) :hangul))
    (is (= (ch->sect \준) :hangul))
    (is (= (ch->sect \힣) :hangul))
    (is (= (ch->sect \a) :ascii))
    (is (= (ch->sect \1) :ascii))
    (is (= (ch->sect \Q) :ascii))
    (is (= (ch->sect \Z) :ascii))
    (is (= (ch->sect \#) :ascii))
    (is (= (ch->sect \ぁ) :kana))
    (is (= (ch->sect \ぜ) :kana))
    (is (= (ch->sect \ん) :kana))
    (is (= (ch->sect \ァ) :kana))
    (is (= (ch->sect \ヅ) :kana))
    (is (= (ch->sect \ヶ) :kana))
    (is (= (ch->sect \←) :arrow))
    (is (= (ch->sect \→) :arrow))
    (is (= (ch->sect \＂) nil))
    (is (= (ch->sect \◆) nil))))

(deftest ch->dist-test
  (testing "Testing ch->dist function ..."
    (is (= (ch->dist \ㄱ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ㅏ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ㅎ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ㅝ) bitmap-font.font/full-dist))
    (is (= (ch->dist \강) bitmap-font.font/full-dist))
    (is (= (ch->dist \가) bitmap-font.font/full-dist))
    (is (= (ch->dist \준) bitmap-font.font/full-dist))
    (is (= (ch->dist \힣) bitmap-font.font/full-dist))
    (is (= (ch->dist \a) bitmap-font.font/half-dist))
    (is (= (ch->dist \1) bitmap-font.font/half-dist))
    (is (= (ch->dist \Q) bitmap-font.font/half-dist))
    (is (= (ch->dist \Z) bitmap-font.font/half-dist))
    (is (= (ch->dist \#) bitmap-font.font/half-dist))
    (is (= (ch->dist \ぁ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ぜ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ん) bitmap-font.font/full-dist))
    (is (= (ch->dist \ァ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ヅ) bitmap-font.font/full-dist))
    (is (= (ch->dist \ヶ) bitmap-font.font/full-dist))
    (is (= (ch->dist \←) bitmap-font.font/full-dist))
    (is (= (ch->dist \→) bitmap-font.font/full-dist))
    (is (= (ch->dist \＂) 0))
    (is (= (ch->dist \◆) 0))))

(deftest calc-font-cell-test
  (testing "Testing calc-font-cell function ..."
    (is (= (calc-font-cell font-ascii-light 0)
           [0 0 1/16 1/8]))
    (is (= (calc-font-cell font-ascii-plain 50)
           [1/8 3/8 3/16 1/2]))
    (is (= (calc-font-cell font-ascii-light 51)
           [3/16 3/8 1/4 1/2]))
    (is (= (calc-font-cell font-ascii-plain 127)
           [15/16 7/8 1 1]))
    (is (= (calc-font-cell font-kana-plain 0)
           [0 0 1/16 1/16]))
    (is (= (calc-font-cell font-kana-light 127)
           [15/16 7/16 1 1/2]))
    (is (= (calc-font-cell font-kana-plain 255)
           [15/16 15/16 1 1]))
    (is (= (calc-font-cell font-kana-light 50)
           [1/8 3/16 3/16 1/4]))
    (is (= (calc-font-cell font-kana-plain 51)
           [3/16 3/16 1/4 1/4]))))

(deftest calc-order-in-sect-test
  (testing "Testing calc-order-in-sect function ..."
    (is (= (calc-order-in-sect \A :ascii) 65))
    (is (= (calc-order-in-sect \z :ascii) 122))
    (is (= (calc-order-in-sect \0 :ascii) 48))
    (is (= (calc-order-in-sect \space :ascii) 32))
    (is (= (calc-order-in-sect \あ :kana) 2))
    (is (= (calc-order-in-sect \ア :kana) 98))))

(deftest divide-string-by-width-test
  (testing "Testing divide-string-by-width function ..."
    (is (= (divide-string-by-width "안녕하세요 반갑습니다." 70)
           ["안녕하세" "요 반갑" "습니다."]))
    (is (= (divide-string-by-width "다람쥐 헌 쳇바퀴에 타고 파." 50)
           ["다람쥐" " 헌 쳇" "바퀴에" " 타고 " "파."]))))

