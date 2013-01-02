(ns cljgame-swing
  (:use cljgame)
  (:use vector))

(def window)
(def component)
(def events (atom nil))
(def ^{:dynamic true} *canvas*)
(def current-object (atom nil))

(def draw-on-screen-now-!)
(def saving-transform!)

(defn swap-and-get-old!
  "Swap! an atom and get old value."
  [atom- value]
  (let [helper (atom nil)]
    (swap! atom- (fn [old]
                   (swap! helper (fn [_] old))
                   value))
    (deref helper)))

(defn emit-ev! [& args]
  (let [ev (apply hash-map args)]
    (swap! events (fn [val] (cons ev val)))))

(defn emit-mouse-ev! [type ev]
  (emit-ev! :cljgame/type type :cljgame/pos (vec2d (.getX ev) (.getY ev))))

(defmethod backend-init-screen! ::swing [backend w h]
  (def component (proxy [javax.swing.JComponent] []
                (paintComponent [canvas] (draw-on-screen-now-! canvas (deref current-object)))))
  (def window (javax.swing.JFrame. "cljgame"))
  (doto component
    (.addMouseListener (proxy [java.awt.event.MouseListener] []
                         (mouseClicked [ev] (emit-mouse-ev! :cljgame/click ev))
                         (mouseEntered [ev] (emit-mouse-ev! :cljgame/mouse-enter ev))
                         (mouseExited [ev] (emit-mouse-ev! :cljgame/mouse-exit ev))
                         (mousePressed [ev] (emit-mouse-ev! :cljgame/mouse-press ev))
                         (mouseReleased [ev] (emit-mouse-ev! :cljgame/mouse-release ev)))))
  (doto window
    (.setContentPane component)
    (.setDefaultCloseOperation javax.swing.JFrame/EXIT_ON_CLOSE)
    (.pack)
    (.setSize w h)
    (.setVisible true)))

(defmethod backend-draw-on-screen! ::swing [backend thing]
  (swap! current-object (fn [past] thing))
  (.repaint window))

(defn draw-on-screen-now-! [canvas thing]
  (binding [*canvas* canvas]
    (when *canvas*
      (.clearRect *canvas* 0 0 (.getWidth window) (.getHeight window))
      (backend-draw! backend thing))))

(defmethod backend-draw! [::swing clojure.lang.ISeq] [backend thing]
  (doseq [item thing] (backend-draw! backend item)))

(defmethod backend-draw! [::swing nil] [backend thing] )

(defmethod backend-draw! [::swing java.awt.image.BufferedImage] [backend thing]
  (.drawImage *canvas* thing nil nil))

(defmethod backend-draw! [::swing :cljgame/scale] [backend thing]
  (saving-transform!
   (.scale *canvas* (:cljgame/by thing) (:cljgame/by thing))
   (backend-draw! backend (:cljgame/item thing))))

(defmethod backend-draw! [::swing :cljgame/translate] [backend thing]
  (saving-transform!
   (.translate *canvas* (:x (:cljgame/by thing)) (:y (:cljgame/by thing)))
   (backend-draw! backend (:cljgame/item thing))))

(defmethod backend-draw! [::swing :cljgame/rectangle] [backend thing]
  (let [shape (:cljgame/shape thing)]
   (.drawRect *canvas* (:x shape) (:y shape) (:w shape) (:h shape))))

(defmethod backend-wait-for-frame! ::swing [backend fps]
  (Thread/sleep (/ 1000 fps)))

(defmethod backend-pop-events! ::swing [backend]
  (swap-and-get-old! events nil))

(defmethod backend-get-image ::swing [backend fn]
  (javax.imageio.ImageIO/read (java.io.File. fn)))

(defmacro saving-transform! [& body]
  `(do
     (let [saved (.getTransform *canvas*)]
       (try
         ~@body
         (finally (.setTransform *canvas* saved))))))
