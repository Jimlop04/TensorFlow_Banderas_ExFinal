package org.tensorflow.lite.examples.classification.Model

import java.io.Serializable

class GeoRectangle(val West: Double, val East: Double, val North: Double, val South: Double):
    Serializable {
}