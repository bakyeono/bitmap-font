(ns bitmap-font.font
  (:import [org.lwjgl.opengl GL11])
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:require [bitmap-font.conf :as conf]  
            [bitmap-font.gl :as gl]  
            [bitmap-font.han :as han]))

;; half-dist, full-dist, line-dist
;; 글자 간격(dist), 줄 간격(line-dist) 상수
(def ^:const half-dist 8)
(def ^:const full-dist 16)
(def ^:const line-dist 20)

;; Font
;; 글꼴별 그리기에 필요한 정보를 담는 레코드
(defrecord Font [img ch-width ch-height cols rows])
(def ^:const font-ascii-plain (Font. :ascii-plain 8 16 16 8))
(def ^:const font-ascii-light (Font. :ascii-light 8 16 16 8))
(def ^:const font-hangul-plain (Font. :hangul-plain 16 16 32 16))
(def ^:const font-hangul-light (Font. :hangul-light 16 16 32 16))
(def ^:const font-kana-plain (Font. :kana-plain 16 16 16 16))
(def ^:const font-kana-light (Font. :kana-light 16 16 16 16))
(def ^:const font-arrow (Font. :arrow 16 16 16 1))

;; 글꼴 이미지들
(declare imgs)

;; load-font-imgs!
;; 글꼴 이미지 로드
(defn load-font-imgs!
  []
  (def imgs
    {:ascii-plain (gl/load-texture "PNG" (:font-ascii-plain conf/options))
     :ascii-light (gl/load-texture "PNG" (:font-ascii-light conf/options))
     :hangul-plain (gl/load-texture "PNG" (:font-hangul-plain conf/options))
     :hangul-light (gl/load-texture "PNG" (:font-hangul-light conf/options))
     :kana-plain (gl/load-texture "PNG" (:font-kana-plain conf/options))
     :kana-light (gl/load-texture "PNG" (:font-kana-light conf/options))
     :arrow (gl/load-texture "PNG" (:font-arrow conf/options))}))

;; Face
;; 서체별 글꼴 지정자
(defrecord Face [ascii hangul hangul-jamo kana arrow])
;; face-plain : 보통 글씨체, 조금 딱딱한 느낌.
(def ^:const
  face-plain (Face. font-ascii-plain
                    font-hangul-plain
                    font-hangul-plain
                    font-kana-plain
                    font-arrow))
;; face-light : 얇은 글씨체, 가벼운 느낌.
(def ^:const
  face-light (Face. font-ascii-light
                    font-hangul-light
                    font-hangul-light
                    font-kana-light
                    font-arrow))

;; ch->sect
;; 문자(ch)의 유니코드에서의 할당구간(sect)을 확인해 반환한다.
;; 구간에 따라 글꼴 데이터 유형과 글꼴을 그리는 방식이 다르다.
(defn ch->sect
  [ch]
  (let [code (.hashCode ch)]
    (cond (and (<= 0x0000 code) (<= code 0x007f)) :ascii
          (and (<= 0xac00 code) (<= code 0xd7a3)) :hangul
          (and (<= 0x3131 code) (<= code 0x3163)) :hangul-jamo
          (and (<= 0x3040 code) (<= code 0x30ff)) :kana
          (and (<= 0x2190 code) (<= code 0x2199)) :arrow
          :else nil)))

;; sect->dist
;; 문자 구간(sect)별 글자 간격(dist)
(def ^:const
  sect->dist {:ascii half-dist
              :hangul full-dist
              :hangul-jamo full-dist
              :kana full-dist
              :arrow full-dist
              nil 0})

;; 문자(ch)에 맞는 글자 간격(dist)을 반환한다.
(defn ch->dist
  [ch]
  (sect->dist (ch->sect ch)))

;; 문자 구간별 유니코드에서의 할당범위 시작값
(def ^:const
  sect-base {:ascii 0x0000
             :hangul 0xac00
             :hangul-jamo 0x3131
             :kana 0x3040
             :arrow 0x2190})

;; calc->font-cell
;; 문자 번호(idx), 글꼴(font), 문자 구간(sect)을 입력받아,
;; 문자를 blit하기 위한 글꼴에서의 칸을 벡터 형태로 반환한다.
;; 반환값은 blit-font-img! 에서 사용한다.
(defn calc-font-cell
  [font n]
  (let [cols (:cols font)
        rows (:rows font)
        u (rem n cols)
        v (quot n cols)]
    [(/ u cols)
     (/ v rows)
     (/ (inc u) cols)
     (/ (inc v) rows)]))

;; calc-idx-in-sect
;; 문자(ch)와 문자 구간(sect)을 입력받아,
;; 해당 문자가 문자 구간에서 몇 번째 문자인지를 반환한다.
(defn calc-idx-in-sect
  [ch sect]
  (unchecked-subtract (.hashCode ch) (sect-base sect)))

;; blit-cell!
;; 글꼴 그림을 화면에 복사한다.
(defn blit-cell!
  [x y font color [on-u on-v to-u to-v]]
  (let [to-x (+ x (:ch-width font))
        to-y (+ y (:ch-height font))]
    (GL11/glEnable GL11/GL_TEXTURE_2D)
    (.bind (imgs (:img font)))
    (gl/set-color! color)
    (GL11/glBegin GL11/GL_QUADS)
    (GL11/glTexCoord2d on-u on-v) (GL11/glVertex2d x y)
    (GL11/glTexCoord2d on-u to-v) (GL11/glVertex2d x to-y)
    (GL11/glTexCoord2d to-u to-v) (GL11/glVertex2d to-x to-y)
    (GL11/glTexCoord2d to-u on-v) (GL11/glVertex2d to-x y)
    (GL11/glEnd)))

;; draw-normal-ch!
;; 기본 방식으로 문자 하나를 그린다.
(defn draw-normal-ch!
  [x y font color ch sect]
  (blit-cell!
    x y
    font color
    (calc-font-cell font (calc-idx-in-sect ch sect)))
  nil)

;; draw-hangul!
;; 한글 문자 하나를 그린다.
(defn draw-hangul!
  [x y font color ch]
  (doseq [idx (han/get-jamo-draw-info ch)]
    (blit-cell!
      x y
      font color
      (calc-font-cell font idx)))
  nil)

;; draw-hangul-jamo!
;; 한글 자모 문자 하나를 그린다.
(defn draw-hangul-jamo!
  [x y font color ch]
  (let [part (han/which-part ch)
        idx (+ (or (han/head->head-idx ch)
                   (han/body->body-idx ch)
                   (han/tail->tail-idx ch))
               (cond (= part :head) (* 0x0 0x20)
                     (= part :body) (* 0x8 0x20)
                     (= part :tail) (* 0xc 0x20)))]
    (blit-cell!
      x y
      font color
      (calc-font-cell font idx))
    nil))

;; draw-ch!
;; sect에 따라 문자 하나를 그리는 함수를 호출한다.
(defn draw-ch!
  [x y font color sect ch]
  (condp = sect
    :ascii (draw-normal-ch! x y font color ch :ascii)
    :hangul (draw-hangul! x y font color ch)
    :hangul-jamo (draw-hangul-jamo! x y font color ch)
    :kana (draw-normal-ch! x y font color ch :kana)
    :arrow (draw-normal-ch! x y font color ch :arrow))
  nil)

;; divide-string-by-width-1
;; 문자열(string)을 지정한 폭(w)까지 잘라내 반환한다.
(defn divide-string-by-width-1
  [string w]
  (loop [x 0 to 0]
    (let [ch (get string to)]
      (if (or (< w x) (= ch \newline) (nil? ch))
        (apply str (take (dec to) string))
        (recur (+ x (ch->dist ch)) (inc to))))))

;; collect-line
;; divide-string-by-width 함수에서 사용하는 서브루틴.
;; 원본 문자열(src-string)의 지정 범위(src-on ~ src-to)를
;; 문자열들의 배열인 dst-coll에 추가한다.
(defn- collect-line
  [src-string src-on src-to dst-coll]
  (conj dst-coll
        (apply str
               (drop src-on (take src-to src-string)))))

;; divide-string-by-width
;; 문자열(string)을 지정한 폭(w)에 맞추어 여러 개의 문자열로 분리해
;; 문자열들을 담은 벡터 형식으로 반환한다.
(defn divide-string-by-width
  [string w]
  (loop [coll [] x 0 on 0 to 0]
    (let [ch (get string to)]
      (cond
        (nil? ch) (collect-line string on to coll)
        (= ch \newline) (recur (collect-line string on to coll)
                               0
                               (inc to)
                               (inc to))
        (< w (+ x (ch->dist ch))) (recur (collect-line string on to coll)
                                         (ch->dist ch)
                                         to
                                         (inc to))
        :else (recur coll
                     (+ x (ch->dist ch))
                     on
                     (inc to))))))


;; draw-string!
;; 문자열(string)을 화면상의 [x, y] 지점에 face 서체와 color 색으로 그린다.
(defn draw-string!
  [x y face color string]
  (loop [cur-x x i 0]
    (when-let [ch (get string i)]
      (let [sect (ch->sect ch)]
        (when-not (nil? sect) (draw-ch! cur-x y (sect face) color sect ch))
        (recur (+ cur-x (sect->dist sect)) (inc i))))))

;; draw-paragraph!
;; 문자열(string)을 화면상의 [x, y] 지점에 face 서체와 color 색으로 그린다.
;; 단, 문자열을 출력하는 길이가 w를 초과할 경우 다음 행으로 넘겨 출력한다.
;; 출력하는 행이 h 높이를 넘어가면 더이상 출력하지 않는다.
(defn draw-paragraph!
  [x y w h face color string]
  (doseq [line (map vector
                    (range y (+ y h) line-dist)
                    (divide-string-by-width string w))]
    (draw-string! x (get line 0) face color (get line 1))))

