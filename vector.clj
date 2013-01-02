(ns vector)

(defrecord Vec2d [x y])

(defn vec2d [x y]
  (Vec2d. x y))

(defn vec+ [a b]
  (Vec2d. (+ (:x a) (:x b)) (+ (:y a) (:y b))))

(defn vec- [a b]
  (Vec2d. (- (:x a) (:x b)) (- (:y a) (:y b))))

(defn vec* [a n]
  (Vec2d. (* (:x a) n) (* (:y a) n)))

(defn vec-div [a n]
  (Vec2d. (/ (:x a) n) (/ (:y a) n)))
