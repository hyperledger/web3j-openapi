package com.test.core

import com.test.core.humanstandardtoken.HumanStandardTokenLifecycle
import org.web3j.openapi.core.ContractResource
import javax.annotation.processing.Generated
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Generated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface TestProjectResource : ContractResource {

    @get:Path("humanstandardtoken")
    val humanStandardToken: HumanStandardTokenLifecycle
}
