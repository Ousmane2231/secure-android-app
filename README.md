# Shield Security - Android Device Admin Application

Application Android de sécurité capable de verrouiller un appareil à distance et d'afficher un message dissuasif.

## 🎯 Features

- 🔒 Verrouillage d'appareil via DeviceAdmin
- 🌐 Intégration Supabase pour commandes distantes
- 🛡️ Chiffrement AES-256 des données sensibles
- 📱 Interface utilisateur intuitive
- 📊 Logs sécurisés et chiffrés
- 🚀 CI/CD GitHub Actions automatisé

## 📋 Requirements

- Android SDK 21 (minSdkVersion)
- Android SDK 33 (targetSdkVersion)
- JDK 11+
- Gradle 8.1.2

## 🛠️ Setup

### 1. Clone le repository

```bash
git clone https://github.com/Ousmane2231/secure-android-app.git
cd secure-android-app
```

### 2. Ajouter les GitHub Secrets

Dans Settings > Secrets and variables > Actions, ajouter:

```
SUPABASE_URL=https://votre-projet.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Build local

```bash
./gradlew assembleDebug
```

## 📁 Project Structure

```
secure-android-app/
├── app/
│   ├── src/main/
│   │   ├── java/com/security/shield/
│   │   │   ├── ShieldCheckService.kt
│   │   │   ├── MyAdminReceiver.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── network/
│   │   │   │   ├── SupabaseClient.kt
│   │   │   │   └── SecurityApiService.kt
│   │   │   └── utils/
│   │   │       ├── EncryptionUtils.kt
│   │   │       └── LoggerUtil.kt
│   │   ├── res/
│   │   │   ├── xml/device_admin_policies.xml
│   │   │   └── values/strings.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── .github/workflows/build-and-release.yml
└── README.md
```

## 🔐 Architecture de Sécurité

### DeviceAdmin
- Force-lock capability
- Reset password capability
- Activation sécurisée via Intent

### Supabase Integration
- Communication HTTPS
- Authentification Bearer Token
- Chiffrement de bout en bout (AES-256-GCM)

### Permissions
- BIND_DEVICE_ADMIN
- INTERNET
- ACCESS_FINE_LOCATION (optionnel)
- RECEIVE_BOOT_COMPLETED

## 📲 Installation

### Debug APK
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Release APK
```bash
./gradlew assembleRelease
```

## 🚀 CI/CD Pipeline

Le workflow GitHub Actions automatise:

1. ✅ Checkout du code
2. ✅ Setup JDK 11
3. ✅ Build Debug & Release APK
4. ✅ Unit Tests
5. ✅ Upload Artifacts

## 📊 API Supabase

### Endpoints

**GET** `/rest/v1/lock_status`
```json
{
  "should_lock": true,
  "message": "Device Locked",
  "timestamp": "2026-05-23T10:30:00Z"
}
```

**POST** `/rest/v1/device_status`
```json
{
  "device_id": "ABC123XYZ",
  "is_secured": true,
  "last_check": "2026-05-23T10:30:00Z"
}
```

## 🧪 Tests

```bash
./gradlew test              # Unit tests
./gradlew connectedAndroidTest  # Integration tests
```

## 🐛 Troubleshooting

### Build Errors

**Error: SDK not found**
```bash
# Acceptez les licenses
yes | sdkmanager --licenses
```

**Error: Gradle daemon failure**
```bash
./gradlew --stop
./gradlew clean build
```

## 📝 License

MIT License - voir LICENSE.md

## 🤝 Contributing

Les pull requests sont bienvenues!

1. Fork le repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit vos changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📞 Support

Pour les questions: ousmane2231@github.com
