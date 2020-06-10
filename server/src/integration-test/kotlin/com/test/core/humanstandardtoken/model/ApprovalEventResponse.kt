package com.test.core.humanstandardtoken.model

import java.math.BigInteger
import kotlin.String

data class ApprovalEventResponse(
  val _owner: String,
  val _spender: String,
  val _value: BigInteger
)
