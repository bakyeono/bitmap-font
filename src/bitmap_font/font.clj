(ns bitmap-font.font
  (:require [bitmap-font.color :as color]
            [bitmap-font.util :as util]
            [bitmap-font.han :as han]))

;;; things about drawing font and processing words
(def ^:const code-base
  {:ascii 0x000
   :hiragana 135
   :katakana 218
   :hangul-jamo 304
   :hangul 356
   :arrow 11528})

;; 각 글자의 크기와 글자 간격 상수
(def ^:const ch-half-width 8)
(def ^:const ch-full-width 16)
(def ^:const ch-height 16)
(def ^:const ch-half-dist 8)
(def ^:const ch-full-dist 16)
(def ^:const ch-line-dist 20)

;; 글꼴 유형(문자셋)별 격자 배열 상수
(def ^:const ascii-cols 16)
(def ^:const ascii-rows 8)
(def ^:const hangul-cols 32)
(def ^:const hangul-rows 16)
(def ^:const kana-cols 16)
(def ^:const kana-rows 16)
(def ^:const arrow-cols 16)
(def ^:const arrow-rows 1)

(defn get-charset
  [ch]
  (let [code (.hashCode ch)]
    (cond (and (<= 0x0000 code) (<= code 0x007f)) :ascii
          (and (<= 0xac00 code) (<= code 0xd7a3)) :hangul
          (and (<= 0x3131 code) (<= code 0x3163)) :hangul-jamo
          (and (<= 0x3040 code) (<= code 0x30ff)) :kana
          (and (<= 0x2190 code) (<= code 0x2199)) :arrow
          :else nil)))

(defn ch-dist
  [ch]
  (if (<= (.hashCode ch) 0x7f)
    ch-dist-half
    ch-dist-full))

(defn ch->code
  [ch]
  nil)
  ;; (let [code (.hashCode ch)]
  ;;   (cond (and (<= 0x0 code) (<= code 0x7f)) code
  ;;         (and (<= 0xac00 code) (<= code 0xd7a3)) (+ (- code 0xac00) code-base-hangul)
  ;;         (and (<= 0x3131 code) (<= code 0x3163)) (+ (- code 0x3131) code-base-hangul-jamo)
  ;;         (and (<= 0x3041 code) (<= code 0x3093)) (+ (- code 0x3041) code-base-hiragana)
  ;;         (and (<= 0x30a1 code) (<= code 0x30f6)) (+ (- code 0x30a1) code-base-katakana)
  ;;         (and (<= 0x2190 code) (<= code 0x2199)) (+ (- code 0x2190) code-base-arrow)
  ;;         :else 0)))

(defn ch->font-cell
  [ch]
  (let [code (ch->code ch)
        u (rem code ch-table-cols)
        v (quot code ch-table-cols)]
    [(/ u ch-table-cols)
     (/ v ch-table-rows)
     (/ (inc u) ch-table-cols)
     (/ (inc v) ch-table-rows)]))

(defn divide-string-by-dist-1
  [string w]
  (loop [x 0 to 0]
    (let [ch (get string to)]
      (if (or (< w x) (= ch \newline) (nil? ch))
        (apply str (take (dec to) string))
        (recur (+ x (ch-dist ch)) (inc to))))))

(defn divide-string-by-dist
  [string w]
  (loop [coll [] x 0 from 0 to 0]
    (let [ch (get string to)]
      (cond
        (nil? ch) (conj coll (apply str (drop from (take to string))))
        (= ch \newline) (recur (conj coll (apply str (drop from (take to string))))
                               0
                               (inc to)
                               (inc to))
        (< w (+ x (ch-dist ch))) (recur (conj coll (apply str (drop from (take to string))))
                                        (ch-dist ch)
                                        to
                                        (inc to))
        :else (recur coll
                     (+ x (ch-dist ch))
                     from
                     (inc to))))))

;; (defn get-jamo-code-with-suits
;;   [ch]
;;   (let [[head suit-of-head
;;          body suit-of-body
;;          tail suit-of-tail]
;;         (han/get-jamo-with-suits ch)]
;;     [(+ head (code-base suit-of-head))
;;      (+ body (code-base suit-of-body))
;;      (+ tail (code-base suit-of-tail))]))
;; 
;; (defn draw-ch
;;   [x y ch]
;;   (if (han/is-full-han-code? ch)
;;     (doseq [part []])
;;     (draw-part-of-ch x y ch)))
;; 
;; (defn draw-jamo
;;   [x y ch]
;;   (let [to-x (+ x ch-width)
;;         to-y (+ y ch-height)
;;         [from-u from-v to-u to-v] (ch->font-cell ch)]
;;     (GL11/glBegin GL11/GL_QUADS)
;;     (GL11/glTexCoord2d from-u from-v) (GL11/glVertex2d x    y)
;;     (GL11/glTexCoord2d from-u to-v)   (GL11/glVertex2d x    to-y)
;;     (GL11/glTexCoord2d to-u   to-v)   (GL11/glVertex2d to-x to-y)
;;     (GL11/glTexCoord2d to-u   from-v) (GL11/glVertex2d to-x y)
;;     (GL11/glEnd)))
;; 
;; (defn draw-code
;;   [x y ch]
;;   (let [to-x (+ x ch-width)
;;         to-y (+ y ch-height)
;;         [from-u from-v to-u to-v] (ch->font-cell ch)]
;;     (GL11/glBegin GL11/GL_QUADS)
;;     (GL11/glTexCoord2d from-u from-v) (GL11/glVertex2d x    y)
;;     (GL11/glTexCoord2d from-u to-v)   (GL11/glVertex2d x    to-y)
;;     (GL11/glTexCoord2d to-u   to-v)   (GL11/glVertex2d to-x to-y)
;;     (GL11/glTexCoord2d to-u   from-v) (GL11/glVertex2d to-x y)
;;     (GL11/glEnd)))
;; 
;; (defn draw-string
;;   [x y face color string]
;;   (GL11/glEnable GL11/GL_TEXTURE_2D)
;;   (.bind (face font-img-dic))
;;   (draw/set-color! color)
;;   (loop [cur-x x i 0]
;;     (when-let [ch (get string i)]
;;       (draw-ch cur-x y ch)
;;       (recur (+ cur-x (ch-dist ch)) (inc i)))))
;; 
;; (defn draw-paragraph
;;   [x y w h face color string]
;;   (doseq [line (map #(vector % %2)
;;                     (range y (+ y h) ch-dist-line)
;;                     (divide-string-by-dist string w))]
;;     (draw-string x (get line 0) face color (get line 1))))
;; 
