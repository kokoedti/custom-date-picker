import { NativeModules } from "react-native";

declare module 'react-native-custom-date-picker' {
    const { CustomDatePicker } = NativeModules;
    export default CustomDatePicker
}