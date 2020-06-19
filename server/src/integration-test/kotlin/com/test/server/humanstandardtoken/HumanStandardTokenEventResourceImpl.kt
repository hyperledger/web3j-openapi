/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.test.server.humanstandardtoken

import com.test.core.humanstandardtoken.HumanStandardTokenEventResource
import com.test.core.humanstandardtoken.HumanStandardTokenResource
import com.test.wrappers.HumanStandardToken
import javax.annotation.processing.Generated
import javax.inject.Singleton

/**
 * Redefined only for compilation purposes until generation is implemented.
 *
 * This subclass shouldn't be a new resource,
 * all methods and values should be in [HumanStandardTokenResourceImpl].
 */
@Generated
@Singleton // FIXME Why Singleton?
class HumanStandardTokenEventResourceImpl(
    private val humanStandardToken: HumanStandardToken
) : HumanStandardTokenEventResource,
    HumanStandardTokenResource by HumanStandardTokenResourceImpl(
        humanStandardToken
    ) {

    override val transferEvents = TransferEventResourceImpl(humanStandardToken)
    override val approvalEvents = ApprovalEventResourceImpl(humanStandardToken)
}
