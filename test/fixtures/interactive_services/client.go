package main

import (
	"fmt"
	"io"
	"net"
	"os"
	"strings"
	"time"
)

func main() {
	if len(os.Args) != 3 {
		fmt.Fprintln(os.Stderr, "usage: client socket|nrepl HOST:PORT")
		os.Exit(2)
	}
	connection, err := net.DialTimeout("tcp", os.Args[2], 2*time.Second)
	if err != nil {
		panic(err)
	}
	defer connection.Close()
	_ = connection.SetDeadline(time.Now().Add(3 * time.Second))

	switch os.Args[1] {
	case "socket":
		response, err := readUntil(connection, "user=>")
		if err != nil {
			panic(fmt.Sprintf("missing initial prompt: %q (%v)", response, err))
		}
		_, _ = io.WriteString(connection, "(+ 1 2)\n")
		response, err = readUntil(connection, "user=>")
		if err != nil || !strings.Contains(response, "3\n") {
			panic(fmt.Sprintf("bad socket REPL response: %q (%v)", response, err))
		}
	case "nrepl":
		_, _ = io.WriteString(connection, "d4:code7:(+ 1 2)2:op4:evale")
		response, err := readUntil(connection, "4:done")
		if !strings.Contains(response, "5:value1:3") ||
			!strings.Contains(response, "4:done") {
			panic(fmt.Sprintf("bad nREPL response: %q (%v)", response, err))
		}
	default:
		fmt.Fprintln(os.Stderr, "unknown protocol:", os.Args[1])
		os.Exit(2)
	}
}

func readUntil(connection net.Conn, marker string) (string, error) {
	var response strings.Builder
	buffer := make([]byte, 256)
	for !strings.Contains(response.String(), marker) {
		count, err := connection.Read(buffer)
		if count > 0 {
			response.Write(buffer[:count])
		}
		if err != nil {
			return response.String(), err
		}
	}
	return response.String(), nil
}
