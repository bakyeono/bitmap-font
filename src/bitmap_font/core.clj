;; bitmap-font.core
(ns bitmap-font.core
  (:import [org.lwjgl.opengl Display])
  (:require [bitmap-font.font :as font]
            [bitmap-font.conf :as conf]
            [bitmap-font.demo :as demo]
            [bitmap-font.gl :as gl]) 
  (:gen-class
    :name bitmap-font.core
    :methods [#^{:static true} [start [] Void]]))

(def ^:const application-title "Bitmap Font Demo")
(def ^:const application-author "Bak Yeon O")

;; init!
;; 프로그램을 초기화한다.
(defn init!
  []
  (conf/init-conf!)
  (gl/init-screen! application-title)
  (font/load-font-imgs!) 
  (gl/init-gl!))

;; clean-up!
;; 프로그램을 정리한다.
(defn clean-up!
  []
  (Display/destroy))

;; -start
;; 프로그램 시작지점
(defn -start
  []
  (init!)
  (demo/run!)
  (clean-up!))


(defn -test
  []
  (let [t (Thread. -start)]
    (.start t)
    t))


