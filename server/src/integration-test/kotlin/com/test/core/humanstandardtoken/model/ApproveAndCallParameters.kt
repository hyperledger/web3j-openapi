package com.test.core.humanstandardtoken.model

import java.math.BigInteger
import kotlin.ByteArray
import kotlin.String

data class ApproveAndCallParameters(
  val _spender: String,
  val _value: BigInteger,
  val _extraData: ByteArray
)
