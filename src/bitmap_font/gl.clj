;; LWJGL 유틸리티
(ns bitmap-font.gl
  (:import [org.lwjgl.opengl Display DisplayMode GL11])
  (:import [org.lwjgl.input Keyboard])
  (:import [org.lwjgl Sys]) 
  (:import [org.newdawn.slick.opengl Texture TextureLoader])
  (:import [org.newdawn.slick.util ResourceLoader])
  (:require [bitmap-font.color :as color]
            [bitmap-font.conf :as conf]))

;; init-screen!
;; GL의 화면 설정을 초기화한다.
(defn init-screen!
  [title]
  (def display-mode nil)
  (Display/setFullscreen (conf/options :fullscreen?))
  (let [d (Display/getAvailableDisplayModes)]
    (doseq [i d]
      (when (and
              (= (conf/options :screen-w) (.getWidth i))
              (= (conf/options :screen-h) (.getHeight i))
              (= (conf/options :screen-bpp) (.getBitsPerPixel i)))
        (def display-mode i)))
    (when (nil? display-mode)
      (def display-mode (DisplayMode. (conf/options :screen-w)
                                      (conf/options :screen-h)))))
  (Display/setDisplayMode display-mode)
  (Display/setTitle title)
  (Keyboard/enableRepeatEvents true)
  (Display/create))

;; init-screen!
;; GL의 각종 설정값을 초기화한다.
(defn init-gl!
  []
  (GL11/glDisable GL11/GL_DEPTH_TEST)
  (GL11/glEnable GL11/GL_BLEND)
  (GL11/glBlendFunc GL11/GL_SRC_ALPHA GL11/GL_ONE_MINUS_SRC_ALPHA)
  (GL11/glShadeModel GL11/GL_SMOOTH)
  (GL11/glClearColor 1.0 1.0 1.0 0.0)
  (GL11/glViewport 0 0 (conf/options :screen-w) (conf/options :screen-h))
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (GL11/glOrtho 0 (conf/options :screen-w) (conf/options :screen-h) 0 1 -1)
  (GL11/glMatrixMode GL11/GL_MODELVIEW))

;; load-texture
;; img-path 경로의 파일을 읽어 img-format 형식으로 해석해
;; GL용 텍스처로 반환한다.
;; 'img-format' 유효값: "BMP" "PNG"
(defn load-texture
  [img-format img-path]
  (TextureLoader/getTexture
    img-format
    (ResourceLoader/getResourceAsStream img-path)))

(defrecord FontImgDic [casual formal])
(defn init-fonts!
  []
  (def font-img-dic
    (FontImgDic.
      (load-texture "PNG" "./img/light-gothic.png")
      (load-texture "PNG" "./img/dkby-dinaru-2.png"))))

;; get-time
;; 시스템의 현재 시각을 반환한다.
(defn get-time
  []
  (/ (* (Sys/getTime) 1000) (Sys/getTimerResolution)))

;; set-color!
;; GL11/glColor3d를 호출하여 GL의 색상 상태를 변경한다.
;; 인수: [r g b] (r, g, b는 각각 0 ~ 255 범위의 실수)
(defn set-color!
  [[r g b]]
  (GL11/glColor3d r g b))

(defn set-color-by-string!
  [string]
  (when (= (count string) 6)
    (let [[r g b] (partition 2 string)]
      (GL11/glColor3d (Integer/parseInt (reduce str r), 16)
                      (Integer/parseInt (reduce str g), 16)
                      (Integer/parseInt (reduce str b), 16)))))

;; draw-box!
;; [x, y] 위치에 w 넓이, h 높이의 사각형을 color 색으로 채워 그린다.
(defn draw-box!
  [x y w h color] 
  (let [to-x (+ x w)
        to-y (+ y h)]
    (GL11/glDisable GL11/GL_TEXTURE_2D)
    (set-color! color)
    (GL11/glBegin GL11/GL_QUADS)
    (GL11/glVertex2d x    y)
    (GL11/glVertex2d x    to-y)
    (GL11/glVertex2d to-x to-y)
    (GL11/glVertex2d to-x y)
    (GL11/glEnd)))

;; draw-graph-hbar!
;; [x, y] 위치에 w 넓이, h 높이의 사각형 가로 막대 그래프를
;; current-val-color max-val-color 색으로 채워 그린다.
;; 그래프의 내용은 current-val과 max-val의 비율이다.
(defn draw-graph-hbar!
  [x y w h current-val-color max-val-color current-val max-val]
  (let [to-x (+ x w)
        to-y (+ y h)
        current-to-x (+ x (* w (/ current-val max-val)))]
    (GL11/glDisable GL11/GL_TEXTURE_2D)
    (set-color! max-val-color)
    (GL11/glBegin GL11/GL_QUADS)
    (GL11/glVertex2d x y)
    (GL11/glVertex2d x to-y)
    (GL11/glVertex2d to-x to-y)
    (GL11/glVertex2d to-x y)
    (GL11/glEnd)
    (set-color! current-val-color)
    (GL11/glBegin GL11/GL_QUADS)
    (GL11/glVertex2d x y)
    (GL11/glVertex2d x to-y)
    (GL11/glVertex2d current-to-x to-y)
    (GL11/glVertex2d current-to-x y)
    (GL11/glEnd)))

