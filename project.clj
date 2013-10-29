(defn get-native-lib-path
  [os-name]
  (cond
   (re-find #"(?i)mac" os-name) "native/macosx"
   (re-find #"(?i)win" os-name) "native/windows"
   (re-find #"(?i)linux" os-name) "native/linux"
   (re-find #"(?i)solaris" os-name) "native/solaris"))

(defproject bitmap-font "0.1.0"
  :description "This library renders bitmap fonts in LWJGL."
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :jvm-opts [~(str "-Djava.library.path=" (get-native-lib-path (System/getProperty "os.name")))]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.lwjgl.lwjgl/lwjgl "2.8.4"]
                 [org.lwjgl.lwjgl/lwjgl_util "2.8.4"]
                 [org.clojars.jyaan/slick "247.1"]
                 [org.clojars.bagucode/jorbis "0.0.17"]
                 [org.clojars.aseipp/jogg "0.0.7"]]
  :main bitmap-font.core)

