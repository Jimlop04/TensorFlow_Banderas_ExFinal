package org.tensorflow.lite.examples.classification.Model

import java.io.Serializable

class Capital(val Name: String, val GeoPt: Array<Double>)
    : Serializable {
}