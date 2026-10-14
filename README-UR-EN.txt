# My Shop Android starter project

## What this starter includes
- App name: My Shop
- Five language UI labels: Portuguese, Pashto, Roman Urdu, Urdu, English
- Add products with name, custom category, purchase price, sale price, quantity
- Local product storage on this phone
- Potential profit and stock quantity summary
- WhatsApp order/enquiry link (enter the shop number with country code, digits only; e.g. 923001234567)

## Important limitations
- This is source code, not a compiled APK.
- Products are stored on one phone only. Customers on other phones cannot see them yet.
- A production online catalogue needs a hosted database/backend and security rules.
- The WhatsApp button opens a WhatsApp/web link; actual checkout/payment is not implemented.
- This starter has not been compiled or tested in Android Studio in this environment.

## Build an APK on a computer
1. Install Android Studio from the official Android developer website.
2. Extract this ZIP.
3. In Android Studio choose Open and select the extracted `MyShopAndroidProject` folder.
4. Let Gradle sync and install any SDK components Android Studio requests.
5. Connect your Android phone and enable Developer options > USB debugging, then press Run to test.
6. To build an APK: Build > Build Bundle(s) / APK(s) > Build APK(s).
7. Android Studio will show the APK location, usually `app/build/outputs/apk/debug/app-debug.apk`.
8. Transfer the APK to your phone and install it. Android may ask you to allow installs from that source.

## If you only have a phone
Building a native APK is much harder on a phone. Use a computer with Android Studio, or use a reputable cloud build service after reviewing its account/privacy requirements. Do not install APKs from unknown websites.


## Build from an Android phone using GitHub Actions (no local Android Studio)
1. Create/sign in to a GitHub account in Chrome.
2. Create a new repository named `my-shop-android`. Keep it private if you prefer.
3. Upload the extracted project files/folders to the repository root using GitHub's web upload. Make sure `.github/workflows/build-apk.yml` is included. GitHub's browser upload can be awkward for folders; if it is too difficult on mobile, ask someone with a computer to upload the project once.
4. Open the repository's Actions tab. Enable Actions if GitHub asks.
5. Open `Build My Shop APK` and tap `Run workflow` (or push files to `main` to trigger it).
6. Wait for the workflow to finish. Open the successful run and find Artifacts; download `My-Shop-debug-APK`.
7. Extract the downloaded artifact ZIP in your phone's Files app, tap `app-debug.apk`, and follow Android's install prompts. Only install the APK you built from your own repository.
8. If the build fails, open the failed run and review the logs; the starter project may need build fixes.

Note: GitHub Actions requires a GitHub account and internet. Usage limits and account requirements may change. This is a starter/testing build, not yet a secure shared online shop.
