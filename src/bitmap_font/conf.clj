; bitmap-font.conf
;; 프로그램 실행 환경 설정
(ns bitmap-font.conf
  (:import [java.io File FileNotFoundException]))

;; conf-file-path
;; 실행 환경 설정 파일의 경로
(def ^:const conf-file-path "./conf")

;; options
;; 실행 환경 설정의 기본값
(def options
  {:screen-w 800
   :screen-h 600
   :screen-bpp 24
   :fullscreen? false
   :fps-limit 60
   :font-ascii-plain "font/ascii-plain.png"
   :font-ascii-light "font/ascii-light.png"
   :font-hangul-plain "font/hangul-dkby-dinaru-2.png"
   :font-hangul-light "font/hangul-light-gothic.png"
   :font-kana-plain "font/kana-plain.png"
   :font-kana-light "font/kana-plain.png"
   :font-arrow "font/arrow.png"})

;; parse-property
;; 설정 파일 내의 개별 설정 항목을
;; [string string] 형태로 입력받아,
;; [keyword parsed-value] 형태로 반환한다.
;; load-user-conf! 함수에서 사용.
(defn parse-property
  [[k v]]
  [(keyword k)
   (condp = (-> (keyword k) options class)
     Boolean (Boolean/parseBoolean v)
     Long (Long/parseLong v)
     Integer (Integer/parseInt v)
     String v
     (str "invalid option : " v))])

;; load-user-conf!
;; 실행 환경 설정 파일(path)을 해석하여 options를 재정의한다.
;; 실행 환경 설정 파일은 java properties 형식이어야 한다.
(defn load-user-conf!
  [path]
  (with-open [reader (clojure.java.io/reader path)]
    (let [properties (doto (java.util.Properties.)
                       (.load reader))]
      (def options (into options (for [p properties]
                                   (parse-property p)))))))

;; init-conf!
;; 실행 환경 설정을 초기화한다.
(defn init-conf!
  []
  (load-user-conf! conf-file-path))

