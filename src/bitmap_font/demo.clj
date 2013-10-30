;; bitmap-font.demo
(ns bitmap-font.demo
  (:import [org.lwjgl.opengl Display GL11])
  (:import [org.lwjgl.input Keyboard])
  (:require [bitmap-font.color :as color]
            [bitmap-font.font :as font]
            [bitmap-font.conf :as conf]
            [bitmap-font.gl :as gl]))

;; render!
;; 화면에 그릴 내용
(defn render!
  []
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT)
  (font/draw-string! 20 20
                     font/face-plain
                     color/brick-red
                     "다람쥐 헌 쳇바퀴에 타고파.")
  (font/draw-string! 20 40
                     font/face-light
                     color/eggplant
                     "닭 잡아서 치킨파티 함")
  (font/draw-string! 20 60
                     font/face-plain
                     color/blueberry
                     "\"웬 초콜릿? 제가 원했던 건 뻥튀기 쬐끔과 의류예요.\" \"얘야, 왜 또 불평?\"")
  (font/draw-string! 20 80
                     font/face-plain
                     color/fiery-rose
                     "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎㄲㄸㅃㅆㅉㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣㅐㅔㅒㅖㅘㅝㅙㅞㅚㅟㅢ")
  (font/draw-string! 20 100
                     font/face-light
                     color/outer-space
                     "ㄱㄴㄷㄹㅁㅂㅅㅇㅈㅊㅋㅌㅍㅎㄲㄸㅃㅆㅉㅏㅑㅓㅕㅗㅛㅜㅠㅡㅣㅐㅔㅒㅖㅘㅝㅙㅞㅚㅟㅢ")
  (font/draw-string! 20 120
                     font/face-plain
                     color/brick-red
                     "How razorback-jumping frogs can level six piqued gymnasts!")
  (font/draw-string! 20 140
                     font/face-light
                     color/green
                     "The quick brown fox jumps over a lazy dog.")
  (font/draw-string! 140 170
                     font/face-plain
                     color/black
                     "=========== 길이 감지, 자동 줄 바꿈 기능 지원 ==========")
  (font/draw-paragraph! 20 200
                        360 300
                        font/face-plain
                        color/vivid-violet
                     "いろはにほへと
ちりぬるを
わかよたれそ
つねならむ
うゐのおくやま
けふこえて
あさきゆめみし
ゑひもせす
アイウエオ カキクケコ サシスセソ タチツテト ナニヌネノ ハヒフヘホ マミムメモ ヤユヨ ラリルレロ ワヰヱヲ")
  (font/draw-paragraph! 420 200
                        360 300
                        font/face-plain
                        color/tan
                     "가난하다고 해서 외로움을 모르겠는가 너와 헤어져 돌아오는 눈 쌓인 골목길에 새파랗게 달빛이 쏟아지는데.  가난하다고 해서 두려움이 없겠는가 두 점을 치는 소리 방범대원의 호각소리 메밀묵 사려 소리에 눈을 뜨면 멀리 육중한 기계 굴러가는 소리.  가난하다고 해서 그리움을 버렸겠는가 어머님 보고 싶소 수없이 뇌어보지만 집 뒤 감나무에 까치밥으로 하나 남았을 새빨간 감 바람소리도 그려보지만.  가난하다고 해서 사랑을 모르겠는가.  내 볼에 와 닿던 네 입술의 뜨거움 사랑한다고 사랑한다고 속삭이던 네 숨결 돌아서는 내 등뒤에 터지던 네 울음.  가난하다고 해서 왜 모르겠는가 가난하기 때문에 이것들을 이 모든 것들을 버려야 한다는 것을.")
  
  (font/draw-string! 20 500
                     font/face-plain
                     color/tulip
                     "Ho◆w razorback-jumping frogs can level six piqued gymnasts!")

  )

;; end!
;; 실행중인 루프를 종료시키고, 반환값을 설정함
(defn end!
  [value]
  (def running? false)
  (def return-value value))

;; proc-quit!
;; 프로그램 종료 이벤트 처리
(defn proc-quit!
  []
  (when (Display/isCloseRequested)
    (end! :quit)))

;; proc-key!
;; 키보드 입력 이벤트 처리
(defn proc-key!
  []
  (while (Keyboard/next)
    (let [key-state (Keyboard/getEventKey)]
      (when (Keyboard/getEventKeyState)
        (condp = key-state
          Keyboard/KEY_ESCAPE (end! nil)
          Keyboard/KEY_SPACE (end! nil)
          nil)))))

;; running-loop
;; 실행 루프
(defn loop!
  []
  (while running?
    (proc-quit!)
    (proc-key!)
    (render!)
    (Display/update)
    (Display/sync (conf/options :fps-limit))))

;; run!
;; 데모 실행
(defn run!
  []
  (def running? true)
  (def return-value nil)
  (loop!)
  return-value)

