package com.amay.tom.service.events;

public class Remote {

        private TOMCommand command;

        public Remote(TOMCommand command) {
            this.command = command;
        }

//        public Remote() {
//        }

        public void setCommand(TOMCommand command) {
            this.command = command;
        }


        public boolean pressButton() {
            return this.command.executeCommand();
        }
}
