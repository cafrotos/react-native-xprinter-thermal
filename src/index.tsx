import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-xprinter-thermal' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

export interface IXprinterThermal {
  isConnected: () => Promise<boolean>;
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

export const XprinterThermal: IXprinterThermal = NativeModules.XprinterThermal
  ? NativeModules.XprinterThermal
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
