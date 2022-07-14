import * as React from 'react';

import { StyleSheet, View, TextInput, Button } from 'react-native';
import { XprinterThermal } from 'react-native-xprinter-thermal';

export default function App() {
  const [text, setText] = React.useState('');
  const [ip, setIp] = React.useState('192.186.');

  return (
    <View style={styles.container}>
      <TextInput value={ip} onChangeText={(value) => setIp(value)} />
      <Button
        title="Connect"
        onPress={() =>
          XprinterThermal.connectNet(ip, 9100)
            .then((result) => console.log(result))
            .catch((err) => console.log(err))
        }
      />
      <Button
        title="Disconnect"
        onPress={() =>
          XprinterThermal.disconnectNet()
            .then((result) => console.log(result))
            .catch((err) => console.log(err))
        }
      />
      <Button
        title="100x150"
        onPress={() => {
          XprinterThermal.setLabelSize(100, 150);
        }}
      />
      <Button
        title="50x50"
        onPress={() => {
          XprinterThermal.setLabelSize(50, 50);
        }}
      />
      <TextInput value={text} onChangeText={(value) => setText(value)} />
      <Button
        title="Print"
        onPress={() => {
          XprinterThermal.addPrintBarcode(
            50,
            0,
            '128',
            100,
            1,
            0,
            2,
            2,
            '19001009'
          );
          XprinterThermal.addPrintText(50, 150, '2', 0, 1, 1, text);
          XprinterThermal.addPrintQRcode(
            50,
            180,
            'M',
            4,
            'A',
            0,
            'M2',
            'S7',
            '19001009'
          );

          XprinterThermal.print()
            .then((result) => console.log(result))
            .catch((err) => console.log(err));
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
