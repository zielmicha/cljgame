cljgame
=======

Simple gaming library for Clojure (much like PyGame, but functional)

Usage
------

See `test-stateful.clj` code for example usage.
You may want to use [Clojure Pool](http://github.com/zielmicha/clojure-pool) for faster debug-edit-run cycles.

Example
------

This program displays rectangles on window where user has clicked.

    (cljgame/init! :cljgame-swing/swing 500 500)
    (cljgame/loop-stateful! :draw-fn (fn [state]
                                       (map (fn [[x y]] (cljgame/rectangle {:x x :y y :w 5 :h 5})) state))
                            :event-fn (fn [state event]
                                        (if (= (:cljgame/type event) :cljgame/click)
                                          (cons (:cljgame/pos event) state)
                                          state))
                            :frame-fn (fn [state elapsed] state)
                            :state nil)
