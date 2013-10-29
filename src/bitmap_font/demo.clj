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
  (GL11/glClear GL11/GL_COLOR_BUFFER_BIT))

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

