package com.test.core.humanstandardtoken.model

import java.math.BigInteger
import kotlin.String

data class ApproveParameters(
  val _spender: String,
  val _value: BigInteger
)
