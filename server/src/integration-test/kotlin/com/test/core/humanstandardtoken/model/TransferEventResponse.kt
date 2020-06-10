package com.test.core.humanstandardtoken.model

import java.math.BigInteger
import kotlin.String

data class TransferEventResponse(
  val _from: String,
  val _to: String,
  val _value: BigInteger
)
