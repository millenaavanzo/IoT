package br.fiap;

import java.io.IOException;

import org.firmata4j.IODevice;
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.Pin.Mode;
import org.firmata4j.firmata.FirmataDevice;



public class ExemploFirmata4J {
	
	
	public static void main(String[] args) {
		// Contrutor do device Firmata a partir do nome da porta serial
		IODevice device = new FirmataDevice("/dev/ttyACM0");
		
		try {
			device.start();
			// Aguarda a completa inicialização do dispositivo
			device.ensureInitializationIsDone(); 
			
			final Pin d3 = device.getPin(3);
			if(d3.supports(Mode.OUTPUT) && d3.supports(Mode.PWM)) {
				d3.setMode(Mode.PWM);
			}
			
			final Pin a0 = device.getPin(14);
			if(a0.supports(Mode.ANALOG)) {
				a0.setMode(Mode.ANALOG);
			}
			

			device.addEventListener(new IODeviceEventListener() {

			    @Override
				public void onStart(IOEvent event) {
			        // Evento indicando a inicialização da placa
			        System.out.println("O dispositivo está pronto");
			    }
				
			    @Override
			    public void onStop(IOEvent event) {
			        // Evento indicando que o dispositivo desligou corretamente
			        System.out.println("O dispositivo desligou");
			    }

			    @Override
			    public void onPinChange(IOEvent event) {
			        // Aqui é indicada a mudança de valor da porta
			        Pin pin = event.getPin();
			        System.out.println(
			                String.format(
			                    "Porta %d tem valor %d",
			                    pin.getIndex(),
			                    pin.getValue())
			            );
			        if(pin.getIndex() == 14) {
			        	try {
							d3.setValue((1023-pin.getValue())/4);
						} catch (IllegalStateException | IOException e) {
							e.printStackTrace();
						}
			        }
			    }

			    @Override
			    public void onMessageReceive(IOEvent event, String message) {
			        //Aqui são processadas as mensagens vindas do dispositivo
			        System.out.println(message);
			    }
			});
			
			
			char c = '0';
			while(c != 'q' && c!= 'Q') {
				//Aqui vem a lógica que é executada enquanto o dispositivo é utilizado
				
				if(System.in.available() > 0) {
					c = Character.toChars(System.in.read())[0];
				}
			}
			
			device.stop(); // desliga a comunicação com o Arduino
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} 
		
	}

}
