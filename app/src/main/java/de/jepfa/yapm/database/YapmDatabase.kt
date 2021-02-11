package de.jepfa.yapm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.jepfa.yapm.database.dao.EncCredentialDao
import de.jepfa.yapm.database.entity.EncCredentialEntity
import de.jepfa.yapm.model.Password
import de.jepfa.yapm.service.encrypt.SecretService
import de.jepfa.yapm.util.Base64Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [EncCredentialEntity::class],
    version = 1,
    exportSchema = false
)
abstract class YapmDatabase : RoomDatabase() {
    abstract fun credentialDao(): EncCredentialDao

    companion object {
        @Volatile
        private var INSTANCE: YapmDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): YapmDatabase? {
            if (INSTANCE == null) {
                synchronized(YapmDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            YapmDatabase::class.java, "yapm_database"
                        )
                        .addCallback(YapmDatabaseCallback(scope))
                        .build()
                    }
                }
            }
            return INSTANCE
        }
    }

    private class YapmDatabaseCallback(
            private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(database: YapmDatabase) {
            // Delete all content here.
            database.clearAllTables()

            val credentialDao = database.credentialDao()

            val secretService = SecretService()
            val key = secretService.getAndroidSecretKey("test-key")

            val encName1 = secretService.encryptCommonString(key, "testname1")
            val encAdditionalInfo1 = secretService.encryptCommonString(key, "")
            val encPassword1 = secretService.encryptPassword(key, Password("1234"))
            var entity1 = EncCredentialEntity(null,
                    Base64Util.encryptedToBase64String(encName1),
                    Base64Util.encryptedToBase64String(encAdditionalInfo1),
                    Base64Util.encryptedToBase64String(encPassword1),
                    false)

            val encName2 = secretService.encryptCommonString(key, "testname2")
            val encAdditionalInfo2 = secretService.encryptCommonString(key, "hints")
            val encPassword2 = secretService.encryptPassword(key, Password("777abc"))
            var entity2 = EncCredentialEntity(null,
                    Base64Util.encryptedToBase64String(encName2),
                    Base64Util.encryptedToBase64String(encAdditionalInfo2),
                    Base64Util.encryptedToBase64String(encPassword2),
                    false)

            val encName3 = secretService.encryptCommonString(key, "testname3")
            val encAdditionalInfo3 = secretService.encryptCommonString(key, "bla bla")
            val encPassword3 = secretService.encryptPassword(key, Password("Abcd9!"))
            var entity3 = EncCredentialEntity(null,
                    Base64Util.encryptedToBase64String(encName3),
                    Base64Util.encryptedToBase64String(encAdditionalInfo3),
                    Base64Util.encryptedToBase64String(encPassword3),
                    true)

            credentialDao.insert(entity1)
            credentialDao.insert(entity2)
            credentialDao.insert(entity3)


        }
    }

}