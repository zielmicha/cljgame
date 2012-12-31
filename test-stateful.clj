(ns test-stateful
  (:require cljgame)
  (:require cljgame-swing))

(cljgame/use-backend! :cljgame-swing/swing)
(cljgame/init-screen! 500 500)

(def icon (cljgame/get-image "git_icon.png"))

(defn draw [state]
  (cljgame/scale icon (::num state)))

(defn event [state event]
  (assoc state ::num 1))

(defn frame [state elapsed]
  (assoc state ::num (mod (+ elapsed (::num state)) 1)))

(cljgame/loop-stateful! :draw-fn draw
                        :event-fn event
                        :frame-fn frame
                        :state {::num 0.5})
