package de.jepfa.yapm.ui.changelogin

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.navigation.fragment.findNavController
import de.jepfa.yapm.R
import de.jepfa.yapm.model.Password
import de.jepfa.yapm.model.Session
import de.jepfa.yapm.service.encrypt.SecretService
import de.jepfa.yapm.service.secretgenerator.PassphraseGenerator
import de.jepfa.yapm.service.secretgenerator.PasswordGenerator
import de.jepfa.yapm.ui.SecureActivity
import de.jepfa.yapm.ui.createvault.CreateVaultActivity
import de.jepfa.yapm.usecase.ChangeMasterPasswordUseCase
import de.jepfa.yapm.usecase.ChangePinUseCase
import de.jepfa.yapm.usecase.GenerateMasterPasswordUseCase
import de.jepfa.yapm.util.putEncrypted

class ChangeMasterPasswordActivity : SecureActivity() {

    private var generatedPassword: Password = Password.empty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Session.isDenied()) {
            return
        }

        setContentView(R.layout.activity_change_master_password)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val currentPinTextView: EditText = findViewById(R.id.current_pin)
        val pseudoPhraseSwitch: Switch = findViewById(R.id.switch_use_pseudo_phrase)
        val switchStorePasswd: Switch = findViewById(R.id.switch_store_master_password)

        val generatedPasswdView: TextView = findViewById(R.id.generated_passwd)

        val buttonGeneratePasswd: Button = findViewById(R.id.button_generate_passwd)
        buttonGeneratePasswd.setOnClickListener {
            generatedPassword = GenerateMasterPasswordUseCase.execute(pseudoPhraseSwitch.isChecked)
            generatedPasswdView.text = generatedPassword.debugToString()
        }

        val button = findViewById<Button>(R.id.button_change)
        button.setOnClickListener {

            val currentPin = Password.fromEditable(currentPinTextView.text)

            if (currentPin.isEmpty()) {
                currentPinTextView.setError(getString(R.string.pin_required))
                currentPinTextView.requestFocus()
            }
            else if (generatedPassword.data.isEmpty()) {
                Toast.makeText(it.context, getString(R.string.generate_passwd_first), Toast.LENGTH_LONG).show()
            }
            else {
                val success = ChangeMasterPasswordUseCase.execute(currentPin, generatedPassword, switchStorePasswd.isChecked, this)

                if (success) {
                    val upIntent = Intent(intent)
                    navigateUpTo(upIntent)

                    generatedPassword.clear()

                    Toast.makeText(baseContext, "Master password successfully changed", Toast.LENGTH_LONG).show()
                }
                else {
                    currentPinTextView.setError(getString(R.string.pin_wrong))
                    currentPinTextView.requestFocus()
                }
            }
        }
    }

    override fun lock() {
        recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val upIntent = Intent(intent)
            navigateUpTo(upIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}