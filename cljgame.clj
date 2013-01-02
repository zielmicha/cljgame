(ns cljgame
  (:use vector))

(defmacro defbackendfun [name docstring args]
  (let [backend-name (symbol (str "backend-" name))]
    `(do
       (defmulti ~backend-name ~docstring (fn ~(into ['backend] args) backend))
       (defn ~name ~docstring ~args (~backend-name backend ~@(seq args))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def backend)
(def default-fps 24)

(defn use-backend! [name]
  (def backend name))

(defbackendfun init-screen!
  "Create game window and show it."
  [w h])

(defn init! [backend w h]
  (use-backend! backend)
  (init-screen! w h))

(def backend-draw!)

(defbackendfun draw-on-screen!
  "Draw canvas content on game window."
  [content])

(defbackendfun wait-for-frame!
  "Waits for next frame."
  [fps])

(defbackendfun pop-events!
  "Pops events from input queue."
  [])

(defbackendfun get-image
  "Loads image from file."
  [fn])

(defmulti backend-draw!
  "Draws thing on screen using imperative backend."
  (fn [backend thing] [backend (if (map? thing) (::type thing) (class thing))]))

(defn loop! [& {:keys [draw-fn key-fn]}]
  (while true
    (let [content (draw-fn)]
      (draw-on-screen! content)
      (wait-for-frame! default-fps))))

(defn loop-stateful! [& {:keys [draw-fn event-fn frame-fn state]}]
;  (println draw-fn event-fn frame-fn state)
  (loop [draw-fn draw-fn event-fn event-fn frame-fn frame-fn state state]
      (let [content (draw-fn state)
            events (pop-events!)
            state1 (reduce event-fn state events)
            state2 (frame-fn state1 (/ 1.0 default-fps))] ; TODO: real elapsed time
        (draw-on-screen! content)
        (wait-for-frame! default-fps)
        (recur draw-fn event-fn frame-fn state2))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-time []
  "Returns current time in seconds."
  (double (/ (System/currentTimeMillis) 1000)))

(defn scale [item by]
  "Scales `item` with scale `by`."
  {::type ::scale, ::item item, ::by by})

(defn translate [item vec]
  "Translates `item` by vector `vec`."
  {::type ::translate, ::item item, ::by vec})

(defn rectangle [shape & {:keys [color]}]
  {::type ::rectangle, ::shape shape, ::color color})

(defrecord Event [type])
