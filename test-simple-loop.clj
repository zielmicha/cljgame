(ns test-simple-loop
  (:require cljgame)
  (:require cljgame-swing))

(cljgame/use-backend! :cljgame-swing/swing)
(cljgame/init-screen! 500 500)

(def icon (cljgame/get-image "git_icon.png"))
(defn draw []
  (cljgame/translate
   (cljgame/scale icon (Math/abs (- (mod (cljgame/get-time) 2) 1)))
   [10 10]))

(cljgame/loop! :draw-fn draw)
