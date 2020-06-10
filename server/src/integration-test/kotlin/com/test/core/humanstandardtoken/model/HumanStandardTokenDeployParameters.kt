package com.test.core.humanstandardtoken.model

import java.math.BigInteger
import kotlin.String

data class HumanStandardTokenDeployParameters(
  val _initialAmount: BigInteger,
  val _tokenName: String,
  val _decimalUnits: BigInteger,
  val _tokenSymbol: String
)
