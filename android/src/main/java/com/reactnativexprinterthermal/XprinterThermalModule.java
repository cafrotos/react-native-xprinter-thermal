package com.reactnativexprinterthermal;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterTSC;

import java.util.ArrayList;
import java.util.List;

@ReactModule(name = XprinterThermalModule.NAME)
public class XprinterThermalModule extends ReactContextBaseJavaModule {
  public static final String NAME = "XprinterThermal";

  public static IMyBinder binder;
  public boolean isConnect = false;

  ServiceConnection conn = new ServiceConnection() {

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      // 绑定成功
      binder = (IMyBinder) service;
    }
  };

  private ArrayList<byte[]> printActions = new ArrayList<byte[]>();
  private byte[] labelSize = DataForSendToPrinterTSC.sizeBymm(50,
    50);
  private byte[] labelGap = DataForSendToPrinterTSC.gapBymm(2, 2);

  public XprinterThermalModule(ReactApplicationContext reactContext) {
    super(reactContext);

    ReactApplicationContext reactAppContext = getReactApplicationContext();
    Intent intent = new Intent(reactAppContext, PosprinterService.class);
    reactAppContext.bindService(intent, conn, reactAppContext.BIND_AUTO_CREATE);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void isConnected(Promise promise) {
    promise.resolve(isConnect);
  }

  @ReactMethod
  public void rebindService() {
    ReactApplicationContext reactAppContext = getReactApplicationContext();

    reactAppContext.unbindService(conn);

    Intent intent = new Intent(reactAppContext, PosprinterService.class);
    reactAppContext.bindService(intent, conn, reactAppContext.BIND_AUTO_CREATE);
  }

  // Connect to printer
  @ReactMethod
  public void connectNet(String ip, Integer port, Promise promise) {
    if (binder == null) {
      promise.reject("Xprinter module is not binded!");
      return;
    }
    binder.connectNetPort("192.168.10.221", 9100, new UiExecute() {
      @Override
      public void onsucess() {
        isConnect = true;
        binder.acceptDataFromPrinter(new UiExecute() {
          @Override
          public void onsucess() {
          }

          @Override
          public void onfailed() {
            isConnect = false;
            promise.reject("Not accept data from printer!");
          }
        });
        promise.resolve("Connected!");
      }

      @Override
      public void onfailed() {
        isConnect = false;
        promise.reject("Connect printer failed!");
      }
    });
  }

  // Disconnect to printer
  @ReactMethod
  public void disconnectNet(Promise promise) {
    if (isConnect) {// 如果是连接状态才执行断开操作
      binder.disconnectCurrentPort(new UiExecute() {

        @Override
        public void onsucess() {
          promise.resolve("Disconnect Success");
        }

        @Override
        public void onfailed() {
          promise.reject("CONNECTION_FAILED", "Disconnect Failed");
        }
      });
    } else {
      promise.reject("CONNECTION_FAILED", "The printer is not connected!");
    }
  }

  // Set label size by mm
  @ReactMethod
  public void setLabelSize(double height, double width) {
    this.labelSize = DataForSendToPrinterTSC.sizeBymm(height,
      width);
  }

  // Set label gap by mm
  @ReactMethod
  public void setLabelGap(double vertical, double horizontal) {
    this.labelGap = DataForSendToPrinterTSC.gapBymm(vertical,
      horizontal);
  }

  // Add text to print
  @ReactMethod
  public void addPrintText(int x, int y, String font, int rotation, int xMultiplication,
                           int yMultiplication, String content) {
    printActions.add(DataForSendToPrinterTSC.text(x, y, font, rotation, xMultiplication, yMultiplication, content));
  }

  // Add barcode to print
  @ReactMethod
  public void addPrintBarcode(int x,
                              int y,
                              String codeType,
                              int height,
                              int human,
                              int rotation,
                              int narrow,
                              int wide,
                              String content) {
    printActions.add(DataForSendToPrinterTSC.barCode(
      x, y, codeType, height, human, rotation, narrow, wide,
      content));
  }

  // Add QRcode to print
  @ReactMethod
  public void addPrintQRcode(int x,
                             int y,
                             String eccLevel,
                             int cellWidth,
                             String mode,
                             int rotation,
                             String model,
                             String mask,
                             String content) {
    printActions.add(DataForSendToPrinterTSC.qrCode(x, y, eccLevel, cellWidth, mode, rotation, model, mask, content));
  }

  // Print
  @ReactMethod
  public void print(Promise promise) {
    if (isConnect) {
      binder.writeDataByYouself(new UiExecute() {

        @Override
        public void onsucess() {
          printActions.clear();
          promise.resolve("Printed!");
        }

        @Override
        public void onfailed() {
          printActions.clear();
          promise.reject("PRINT_ERROR", "Cannot print, check native code!");
        }
      }, new ProcessData() {

        @Override
        public List<byte[]> processDataBeforeSend() {
          ArrayList<byte[]> list = new ArrayList<byte[]>();

          list.add(labelSize);
          list.add(labelGap);
          list.add(DataForSendToPrinterTSC.cls());
          list.addAll(printActions);
          list.add(DataForSendToPrinterTSC.print(1));

          return list;
        }
      });
    } else {
      promise.reject("CONNECTION_FAILED", "The printer is not connected!");
    }
  }
}
