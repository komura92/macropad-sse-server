# Macropad SSE server

This Spring microservice is the connection channel of my open source project called "Ninja-Macropad".
It allows to send bidirectional notifications between devices about actions, like switching 
active window on PC or action execution on mobile app. This part is still required till 
bluetooth communication channel implementation.

During free time I will make some docs about this project to make it usable for
those who want to increase productivity level with me.

## Showcase

![Examples of automations](https://github.com/komura92/projects-gallery/blob/master/macropad/gifs/macropad-flutter-showcase.gif)
#
## Configuration

It's recommended to use at least HTTPS encryption to prevent plain text communication with server.
We don't want anybody else to execute commands on our machine, right? :)

## Roadmap

In the future, I want to add some additional features to this project. There's a shortly described list:
- ~~remove event queue [MP-ES-F-01]~~,
- ~~device id is PC hostname [MP-ES-F-02]~~,
- ~~endpoint for getting list of available devices [MP-ES-F-03]~~.
