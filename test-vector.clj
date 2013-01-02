(ns test-vector
  (:use vector))

(def f (vec2d 1 2))
(println f)
(println (vec+ f f))
