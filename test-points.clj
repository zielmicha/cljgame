(ns test-stateful
  (:require cljgame)
  (:require cljgame-swing))

(cljgame/init! :cljgame-swing/swing 500 500)
(cljgame/loop-stateful! :draw-fn (fn [state]
                                   (map (fn [vec] (cljgame/rectangle {:x (:x vec) :y (:y vec) :w 5 :h 5})) state))
                        :event-fn (fn [state event]
                                    (if (= (:cljgame/type event) :cljgame/click)
                                      (cons (:cljgame/pos event) state)
                                      state))
                        :frame-fn (fn [state elapsed] state)
                        :state nil)
