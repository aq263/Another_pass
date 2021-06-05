package de.jepfa.yapm.usecase

import de.jepfa.yapm.ui.SecureActivity
import de.jepfa.yapm.service.PreferenceService

object RemoveStoredMasterPasswordUseCase: SecureActivityUseCase {

    override fun execute(activity: SecureActivity): Boolean {
        PreferenceService.delete(PreferenceService.DATA_ENCRYPTED_MASTER_PASSWORD, activity)
        //Session.logout()

        return true
    }

}