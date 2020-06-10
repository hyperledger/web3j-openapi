package com.test.core.humanstandardtoken.model

import java.math.BigInteger
import kotlin.String

data class TransferParameters(
  val _to: String,
  val _value: BigInteger
)
