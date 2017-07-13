;; bitmap-font.han
;; 한글 처리 유틸리티
(ns bitmap-font.han)

;; --- 한글 자모 분리 및 합성 ---

;; * 유니코드 한글 조합 공식
;;   완성형 한글 코드 =
;;   (((초성번호 * 중성개수) + 중성번호) * 종성개수) + 종성번호 + 0xac00

;; 유니코드 완성형 한글 범위
(def ^:const han-begin 0xac00)
(def ^:const han-end 0xd7a3)

;; 중성의 가지수 / 종성의 가지수
(def ^:const number-of-bodies 21)
(def ^:const number-of-tails 28)
(def ^:const number-of-bodies*tails
  (* number-of-bodies number-of-tails))

;; is-head?
;; 한글 초성 문자 집합
(def ^:const is-head?
  #{\ㄱ \ㄲ \ㄴ \ㄷ \ㄸ \ㄹ \ㅁ \ㅂ \ㅃ \ㅅ \ㅆ
    \ㅇ \ㅈ \ㅉ \ㅊ \ㅋ \ㅌ \ㅍ \ㅎ})

;; is-body?
;; 한글 중성 문자 집합
(def ^:const is-body?
  #{\ㅏ \ㅐ \ㅑ \ㅒ \ㅓ \ㅔ \ㅕ \ㅖ \ㅗ \ㅘ \ㅙ \ㅚ
    \ㅛ \ㅜ \ㅝ \ㅞ \ㅟ \ㅠ \ㅡ \ㅢ \ㅣ})

;; is-tail?
;; 한글 종성 문자 집합
(def ^:const is-tail?
  #{\ㄱ \ㄲ \ㄳ \ㄴ \ㄵ \ㄶ \ㄷ \ㄹ \ㄺ \ㄻ \ㄼ \ㄽ \ㄾ \ㄿ \ㅀ
   \ㅁ \ㅂ \ㅄ \ㅅ \ㅆ \ㅇ \ㅈ \ㅊ \ㅋ \ㅌ \ㅍ \ㅎ})

;; which-part
;; 한글 자모 문자를 입력받아 초성, 중성, 종성 중 어느 것인지 확인한다.
;; 반환값은 :head, :body, :tail, nil 중 하나다.
;; 초성과 종성 모두에 해당하는 경우, 초성으로 간주한다.
(defn which-part
  [ch]
  (cond
    (is-head? ch) :head
    (is-body? ch) :body
    (is-tail? ch) :tail
    :else nil))

;; head->head-idx
;; 한글 초성 문자를 한글 전산 체계 상의 순서(번호)와 대응시킨다.
(def ^:const head->head-idx
  {\ㄱ 0 \ㄲ 1 \ㄴ 2 \ㄷ 3 \ㄸ 4 \ㄹ 5 \ㅁ 6 \ㅂ 7 \ㅃ 8 \ㅅ 9
   \ㅆ 10 \ㅇ 11 \ㅈ 12 \ㅉ 13 \ㅊ 14 \ㅋ 15 \ㅌ 16 \ㅍ 17 \ㅎ 18})

;; body->body-idx
;; 한글 중성 문자를 한글 전산 체계 상의 순서(번호)와 대응시킨다.
(def ^:const body->body-idx
  {\ㅏ 0 \ㅐ 1 \ㅑ 2 \ㅒ 3 \ㅓ 4 \ㅔ 5 \ㅕ 6 \ㅖ 7 \ㅗ 8 \ㅘ 9
   \ㅙ 10 \ㅚ 11 \ㅛ 12 \ㅜ 13 \ㅝ 14 \ㅞ 15 \ㅟ 16 \ㅠ 17 \ㅡ 18 \ㅢ 19
   \ㅣ 20})

;; body->body-idx
;; 한글 종성 문자를 한글 전산 체계 상의 순서(번호)와 대응시킨다.
(def ^:const tail->tail-idx
  {nil 0 \ㄱ 1 \ㄲ 2 \ㄳ 3 \ㄴ 4 \ㄵ 5 \ㄶ 6 \ㄷ 7 \ㄹ 8 \ㄺ 9
   \ㄻ 10 \ㄼ 11 \ㄽ 12 \ㄾ 13 \ㄿ 14 \ㅀ 15 \ㅁ 16 \ㅂ 17 \ㅄ 18 \ㅅ 19
   \ㅆ 20 \ㅇ 21 \ㅈ 22 \ㅊ 23 \ㅋ 24 \ㅌ 25 \ㅍ 26 \ㅎ 27})

;; head-idx->head
;; 번호를 한글 초성 순서(번호)에 따른 문자와 대응시킨다.
(def ^:const head-idx->head
  (apply hash-map
         (interleave (vals head->head-idx)
                     (keys head->head-idx))))

;; body-idx->body
;; 번호를 한글 중성 순서(번호)에 따른 문자와 대응시킨다.
(def ^:const body-idx->body
  (apply hash-map
         (interleave (vals body->body-idx)
                     (keys body->body-idx))))

;; tail-idx->tail
;; 번호를 한글 중성 순서(번호)에 따른 문자와 대응시킨다.
(def ^:const tail-idx->tail
  (apply hash-map
         (interleave (vals tail->tail-idx)
                     (keys tail->tail-idx))))

;; full-han-code->head-idx
;; 형식: 유니코드 -> long
;; 완성형 한글 코드를 입력받아 초성의 번호를 반환한다.
(defn full-han-code->head-idx
  [code tail]
  (quot (quot (- code han-begin tail)
              number-of-tails)
        number-of-bodies))

;; full-han-code->body-idx
;; 형식: 유니코드 -> long
;; 완성형 한글 코드를 입력받아 중성의 번호를 반환한다.
(defn full-han-code->body-idx
  [code tail]
  (rem (quot (- code han-begin tail)
             number-of-tails)
       number-of-bodies))

;; full-han-code->tail-idx
;; 형식: 유니코드 -> long
;; 완성형 한글 코드를 입력받아 종성의 번호를 반환한다.
(defn full-han-code->tail-idx
  [code]
  (rem (unchecked-subtract code han-begin)
       number-of-tails))

;; jamo-idxs->full-han-code
;; 초성번호(head), 중성번호(body), 종성번호(tail)를 입력받아,
;; 대응하는 유니코드 완성형 한글 코드값을 반환한다.
(defn jamo-idxs->full-han-code
  [head body tail]
  (+ han-begin
     tail
     (unchecked-multiply body number-of-tails)
     (unchecked-multiply head number-of-bodies*tails)))

;; jamo->full-han-code
;; 초성(head-ch), 중성(body-ch), 종성(tail-ch)을 조합해
;; 유니코드 완성형 한글 코드값을 반환한다.
(defn jamo->full-han-code
  [head-ch body-ch tail-ch]
  (jamo-idxs->full-han-code (head->head-idx head-ch)
                            (body->body-idx body-ch)
                            (tail->tail-idx tail-ch)))

;; jamo->full-han
;; 초성(head-ch), 중성(body-ch), 종성(tail-ch)을 조합해
;; 유니코드 완성형 한글 문자를 반환한다.
(defn jamo->full-han
  [head-ch body-ch tail-ch]
  (char (jamo->full-han-code head-ch body-ch tail-ch)))

;; is-full-han-code?
;; code가 유니코드 완성형 한글코드인가?
(defn is-full-han-code?
  [code]
  (and (<= han-begin code)
       (<= code han-end)))

;; is-full-han?
;; ch가 유니코드 완성형 한글코드인가?
(defn is-full-han?
  "글자가 유니코드 완성형 한글코드인지 검사한다."
  [ch]
  (is-full-han-code? (.hashCode ch)))

;; full-han-code->jamo-idxs
;; 유니코드 완성형 한글 코드값(code)을
;; 초성, 중성, 종성의 각 자모로 분리해 그 번호를 반환한다.
;; code가 올바른 범위인지는 검사하지 않으므로
;; is-full-han?을 이용해 미리 검사해야 한다.
;; 반환값: [초성번호 중성번호 종성번호]
(defn full-han-code->jamo-idxs
  [code]
  (let [tail (full-han-code->tail-idx code)
        body (full-han-code->body-idx code tail)
        head (full-han-code->head-idx code tail)]
    [head body tail]))

;; full-han->jamo-idxs
;; 유니코드 완성형 한글 문자를
;; 초성, 중성, 종성의 각 자모를 분리해 그 번호를 반환한다.
(defn full-han->jamo-idxs
  [ch]
  (full-han-code->jamo-idxs (.hashCode ch)))

;; full-han->jamo
;; 유니코드 완성형 한글 문자를
;; 초성, 중성, 종성의 각 자모를 분리해 문자로 반환한다.
(defn full-han->jamo
  [ch]
  (let [[head body tail] (full-han->jamo-idxs ch)]
    [(head-idx->head head)
     (body-idx->body body)
     (tail-idx->tail tail)]))


;; --- 초중종성 '벌' 분류 ---
;; 한글 폰트 출력을 위해, 초중종성의 '벌'을 분류함.

;; make-suit-matchers
;; make-suit-dic
;; '벌' 테이블 생성을 위한 편의 함수
(defn- make-suit-matchers
  [v ks jamo->idx]
  (for [k ks] [(jamo->idx k) v]))
(defn- make-suit-dic
  [v-kss jamo->idx]
  (into {} (apply
             concat
             (for [[v ks] v-kss]
               (make-suit-matchers (* v 0x20) ks jamo->idx)))))

;; suit-of-head-on-body-without-tail
;; 중성번호에 따른 초성의 벌 (종성이 없을 때)
(def ^:const suit-of-head-on-body-without-tail
  (make-suit-dic
    [[0x0 [\ㅏ \ㅐ \ㅑ \ㅒ \ㅓ \ㅔ \ㅕ \ㅖ \ㅣ]]
     [0x1 [\ㅗ \ㅛ \ㅡ]]
     [0x2 [\ㅜ \ㅠ]]
     [0x3 [\ㅘ \ㅙ \ㅚ \ㅢ]]
     [0x4 [\ㅝ \ㅞ \ㅟ]]]
    body->body-idx))

;; suit-of-head-on-body-with-tail
;; 중성번호에 따른 초성의 벌 (종성이 있을 때)
(def ^:const suit-of-head-on-body-with-tail
  (make-suit-dic
    [[0x5 [\ㅏ \ㅐ \ㅑ \ㅒ \ㅓ \ㅔ \ㅕ \ㅖ \ㅣ]]
     [0x6 [\ㅗ \ㅛ \ㅜ \ㅠ \ㅡ]]
     [0x7 [\ㅘ \ㅙ \ㅚ \ㅝ \ㅞ \ㅟ \ㅢ]]]
    body->body-idx))

;; suit-of-body-on-head-without-tail
;; 초성번호에 따른 중성의 벌 (종성이 없을 때)
(def ^:const suit-of-body-on-head-without-tail
  (make-suit-dic
    [[0x8 [\ㄱ \ㅋ]]
     [0x9 [\ㄲ \ㄴ \ㄷ \ㄸ \ㄹ \ㅁ \ㅂ \ㅃ
           \ㅅ \ㅆ \ㅇ \ㅈ \ㅉ \ㅊ \ㅌ \ㅍ \ㅎ]]]
    head->head-idx))

;; suit-of-body-on-head-with-tail
;; 초성번호에 따른 중성의 벌 (종성이 있을 때)
(def ^:const suit-of-body-on-head-with-tail
  (make-suit-dic
    [[0xa [\ㄱ \ㅋ]]
     [0xb [\ㄲ \ㄴ \ㄷ \ㄸ \ㄹ \ㅁ \ㅂ \ㅃ \ㅅ \ㅆ
           \ㅇ \ㅈ \ㅉ \ㅊ \ㅌ \ㅍ \ㅎ]]]
    head->head-idx))

;; suit-of-tail-on-body
;; 중성번호에 따른 종성의 벌
(def ^:const suit-of-tail-on-body
  (make-suit-dic
    [[0xc [\ㅏ \ㅑ \ㅘ]]
     [0xd [\ㅓ \ㅕ \ㅚ \ㅝ \ㅟ \ㅢ \ㅣ]]
     [0xe [\ㅐ \ㅒ \ㅔ \ㅖ \ㅙ \ㅞ]]
     [0xf [\ㅗ \ㅛ \ㅜ \ㅠ \ㅡ]]]
    body->body-idx))

;; get-suit-of-head
;; 중성번호와 종성번호를 입력받아,
;; 이에 해당하는 초성의 벌을 반환한다.
(defn get-suit-of-head
  [body tail]
  (if (zero? tail)
    (suit-of-head-on-body-without-tail body)
    (suit-of-head-on-body-with-tail body)))

;; get-suit-of-body
;; 초성번호와 종성번호를 입력받아,
;; 이에 해당하는 중성의 벌을 반환한다.
(defn get-suit-of-body
  [head tail]
  (if (zero? tail)
    (suit-of-body-on-head-without-tail head)
    (suit-of-body-on-head-with-tail head)))

;; get-suit-of-tail
;; 중성번호를 입력받아,
;; 해당하는 종성의 벌을 반환한다.
(defn get-suit-of-tail
  [body]
  (suit-of-tail-on-body body))

;; jamo-idxs->jamo-suits
;; 초성번호(head), 중성번호(body), 종성번호(tail)를 입력받아,
;; 초성, 중성, 종성의 벌과 함께 반환한다.
;; [초성번호 초성벌 중성번호 중성벌 종성번호 종성벌] 형태로 반환한다.
(defn jamo-idxs->jamo-suits
  [head body tail]
  [(get-suit-of-head body tail)
   (get-suit-of-body head tail)
   (get-suit-of-tail body)])

;; get-jamo-draw-info
;; 유니코드 한글 완성형 글자 하나를 입력받아,
;; 자모의 순서와 자모의 벌을 합친 값을 반환한다.
;; (글꼴에서 해당 칸을 찾을 때 출력에 사용)
;; 계산식: 8x4x4 글꼴에서
;;         16종의 벌이 0x0~0xf 사이에 배치되어 있을 때,
;;         번호: 벌 * 0x20 + 자모순서
;; 반환형식: [초성번호 중성번호 종성번호]
(defn get-jamo-draw-info
  [ch]
  (let [[h b t] (full-han->jamo-idxs ch)
        [sh sb st] (jamo-idxs->jamo-suits h b t)]
    [(+ h sh) (+ b sb) (+ t st)]))


;; --- qwerty 자판 -> 한글 2벌식 자판 변환 ---

;; qwerty-type->han2-type
;; 한 문자가 쿼티 자판으로 입력되었을 때
;; 같은 위치의 한글 2벌식 자판에 해당하는 문자로 대응시킨다.
(def ^:const qwerty-type->han2-type
  {\a \ㅁ \A \ㅁ \b \ㅠ \B \ㅠ \c \ㅊ \C \ㅊ \d \ㅇ \D \ㅇ
   \e \ㄷ \E \ㄸ \f \ㄹ \F \ㄹ \g \ㅎ \G \ㅎ \h \ㅗ \H \ㅗ
   \i \ㅑ \I \ㅑ \j \ㅓ \J \ㅓ \k \ㅏ \K \ㅏ \l \ㅣ \L \ㅣ
   \m \ㅡ \M \ㅡ \n \ㅜ \N \ㅜ \o \ㅐ \O \ㅒ \p \ㅔ \P \ㅖ
   \q \ㅂ \Q \ㅃ \r \ㄱ \R \ㄲ \s \ㄴ \S \ㄴ \t \ㅅ \T \ㅆ
   \u \ㅕ \U \ㅕ \v \ㅍ \V \ㅍ \w \ㅈ \W \ㅉ \x \ㅌ \X \ㅌ
   \y \ㅛ \Y \ㅛ \z \ㅋ \Z \ㅋ})

;; han2-type->qwerty-type
;; 한 문자가 한글 2벌식 자판으로 입력되었을 때
;; 같은 위치의 쿼티 자판에 해당하는 문자(열)로 대응시킨다.
(def ^:const han2-type->qwerty-type
  {\ㅁ \a \ㅠ \b \ㅊ \c \ㅇ \d \ㄷ \e \ㄸ \E \ㄹ \f \ㅎ \g
   \ㅗ \h \ㅑ \i \ㅓ \j \ㅏ \k \ㅣ \l \ㅡ \m \ㅜ \n \ㅐ \o
   \ㅒ \O \ㅔ \p \ㅖ \P \ㅂ \q \ㅃ \Q \ㄱ \r \ㄲ \R \ㄴ \s
   \ㅅ \t \ㅆ \T \ㅕ \u \ㅍ \v \ㅈ \w \ㅉ \W \ㅌ \x \ㅛ \y
   \ㅋ \z
   \ㅘ "hk" \ㅙ "ho" \ㅚ "hl" \ㅝ "nj" \ㅞ "np" \ㅟ "nl" \ㅢ "ml"
   \ㄳ "rt" \ㄵ "sw" \ㄶ "sg" \ㄺ "fr" \ㄻ "fa" \ㄼ "fq" \ㄽ "ft"
   \ㄾ "fx" \ㄿ "fv" \ㅀ "fg" \ㅄ "qt"})

;; is-body-comb?
;; 2벌식 자판의 중성 조합에 해당하는가?
(def ^:const is-body-comb?
  {\ㅗ #{\ㅏ \ㅐ \ㅣ}
   \ㅜ #{\ㅓ \ㅔ \ㅣ}
   \ㅡ #{\ㅣ}})

;; is-body-comb?
;; 2벌식 자판의 종성 조합에 해당하는가?
(def ^:const is-tail-comb?
  {\ㄱ #{\ㅅ}
   \ㄴ #{\ㅈ \ㅎ}
   \ㄹ #{\ㄱ \ㅁ \ㅂ \ㅅ \ㅌ \ㅍ \ㅎ}
   \ㅂ #{\ㅅ}})

;; body-comb-dic
;; 2벌식 자판의 중성 조합에 따른 결과 사전
(def ^:const body-comb-dic
  {[\ㅗ \ㅏ] \ㅘ [\ㅗ \ㅐ] \ㅙ [\ㅗ \ㅣ] \ㅚ
   [\ㅜ \ㅓ] \ㅝ [\ㅜ \ㅔ] \ㅞ [\ㅜ \ㅣ] \ㅟ
   [\ㅡ \ㅣ] \ㅢ})

;; tail-comb-dic
;; 2벌식 자판의 종성 조합에 따른 결과 사전
(def ^:const tail-comb-dic
  {[\ㄱ \ㅅ] \ㄳ
   [\ㄴ \ㅈ] \ㄵ [\ㄴ \ㅎ] \ㄶ
   [\ㄹ \ㄱ] \ㄺ [\ㄹ \ㅁ] \ㄻ [\ㄹ \ㅂ] \ㄼ [\ㄹ \ㅅ] \ㄽ
   [\ㄹ \ㅌ] \ㄾ [\ㄹ \ㅍ] \ㄿ [\ㄹ \ㅎ] \ㅀ
   [\ㅂ \ㅅ] \ㅄ})

(declare comb-head comb-body comb-tail comb-final)

;; comb-head
;; 한글 2벌식 자판 오토마타- 초성 조합 루틴
(defn- comb-head
  [[a :as letters]]
  (if (is-head? a)
    (comb-body a (rest letters))
    (comb-final a nil nil 1)))

;; comb-body
;; 한글 2벌식 자판 오토마타- 중성 조합 루틴
(defn- comb-body
  [h [b c :as letters]]
  (if (is-body? b)
    (if-let [comb? (is-body-comb? b)]
      (if (comb? c)
        (comb-tail h (body-comb-dic [b c]) (drop 2 letters) 3)
        (comb-tail h b (rest letters) 2))
      (comb-tail h b (rest letters) 2))
    (comb-final h nil nil 1)))

;; comb-tail
;; 한글 2벌식 자판 오토마타- 종성 조합 루틴
(defn- comb-tail
  [h b [d e f] size]
  (if (is-tail? d)
    (if-let [comb? (is-tail-comb? d)]
      (if (comb? e)
        (if (is-body? f)
          (comb-final h b d (inc size))
          (comb-final h b (tail-comb-dic [d e]) (unchecked-add size 2)))
        (if (is-body? e)
          (comb-final h b nil size)
          (comb-final h b d (inc size))))
      (if (is-body? e)
        (comb-final h b nil size)
        (comb-final h b d (inc size))))
    (comb-final h b nil size)))

;; comb-final
;; 한글 2벌식 자판 오토마타- 최종 조합 루틴
(defn- comb-final
  [h b t size]
  (if (nil? b)
    [size h]
    [size (jamo->full-han h b t)]))

;; qwerty->han2-letters
;; 하나의 글자(string)를
;; 쿼티 자판 -> 한글 2벌식 자판으로 대응하여 반환한다.
(defn- qwerty->han2-letters
  [string]
  (map #(if-let [han2 (qwerty-type->han2-type %)]
          han2
          %)
       string))

;; qwerty->han2
;; 문자열(string)을
;; 쿼티 자판 -> 한글 2벌식 자판으로 대응하여 반환한다.
(defn qwerty->han2
  [string]
  (let [letters (qwerty->han2-letters string)]
    (loop [i 0 acc ""]
      (if (< i (count letters))
        (let [[size conv] (comb-head (drop i letters))]
          (recur (unchecked-add i size) (str acc conv)))
        acc))))

;; han2->qwerty-single-ch
;; 하나의 문자(ch)를
;; 한글 2벌식 자판 -> 쿼티 자판으로 대응하여 반환한다.
(defn- han2->qwerty-single-ch
  [ch]
  (if-let [qwerty (han2-type->qwerty-type ch)]
    qwerty
    (let [code (.hashCode ch)]
      (if (and (<= han-begin code)
               (<= code han-end))
        (let [t (full-han-code->tail-idx code)
              b (full-han-code->body-idx code t)
              h (full-han-code->head-idx code t)
              ch-h (head-idx->head h)
              ch-b (char (+ 0x314f b))
              ch-t (tail-idx->tail t)]
          (str (han2-type->qwerty-type ch-h)
               (han2-type->qwerty-type ch-b)
               (han2-type->qwerty-type ch-t)))
        ch))))

;; han2->qwerty
;; 문자열(string)을
;; 한글 2벌식 자판 -> 쿼티 자판으로 대응하여 반환한다.
(defn han2->qwerty
  [string]
  (reduce str (map han2->qwerty-single-ch string)))


