# react-native-xprinter-thermal
Portable xprinter sdk for react native android only
## Installation

```sh
yarn add react-native-xprinter-thermal
```

Add service to ```AndroidManifest.xml```
```xml
      <activity
        android:name=".MainActivity"
        android:label="@string/app_name"
        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|screenSize|smallestScreenSize|uiMode"
        android:launchMode="singleTask"
        android:windowSoftInputMode="adjustResize"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
      </activity>
      <!-- Add service below -->
      <service android:name="net.posprinter.service.PosprinterService" >
      </service>
      <!-- End -->
    </application>
```

## Usage

```ts
import { XprinterThermal } from "react-native-xprinter-thermal";

// ...

// Interface for xprinter module
interface IXprinterThermal {
  connectNet: (ip: string, port: number) => Promise<void>;
  disconnectNet: () => Promise<void>;
  setLabelSize: (height: number, width: number) => void;
  setLabelGap: (vertical: number, horizontal: number) => void;
  addPrintText: (
    x: number,
    y: number,
    font: string,
    rotation: number,
    xMultiplication: number,
    yMultiplication: number,
    content: string
  ) => void;
  addPrintBarcode: (
    x: number,
    y: number,
    codeType: string,
    height: number,
    human: number,
    rotation: number,
    narrow: number,
    wide: number,
    content: string
  ) => void;
  addPrintQRcode: (
    x: number,
    y: number,
    eccLevel: string,
    cellWidth: number,
    mode: string,
    rotation: number,
    model: string,
    mask: string,
    content: string
  ) => void;
  print: () => Promise<string>;
}
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
