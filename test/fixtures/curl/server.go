package main

import (
	"fmt"
	"io"
	"net"
	"net/http"
	"os"
)

func main() {
	if len(os.Args) != 2 {
		panic("address file required")
	}

	listener, err := net.Listen("tcp", "127.0.0.1:0")
	if err != nil {
		panic(err)
	}

	mux := http.NewServeMux()
	mux.HandleFunc("/request", func(writer http.ResponseWriter, request *http.Request) {
		writer.Header().Add("X-Gobb-Test", "one")
		writer.Header().Add("X-Gobb-Test", "two")
		fmt.Fprintf(
			writer,
			"%s|%s|%s",
			request.Method,
			request.URL.Query().Get("message"),
			request.Header.Get("X-Request-Test"),
		)
	})
	mux.HandleFunc("/echo", func(writer http.ResponseWriter, request *http.Request) {
		_, _ = io.Copy(writer, request.Body)
	})
	mux.HandleFunc("/redirect", func(writer http.ResponseWriter, request *http.Request) {
		http.Redirect(writer, request, "/request", http.StatusFound)
	})
	mux.HandleFunc("/missing", func(writer http.ResponseWriter, request *http.Request) {
		writer.WriteHeader(http.StatusNotFound)
		_, _ = writer.Write([]byte("missing"))
	})

	if err := os.WriteFile(
		os.Args[1],
		[]byte("http://"+listener.Addr().String()),
		0o600,
	); err != nil {
		panic(err)
	}

	if err := http.Serve(listener, mux); err != nil {
		panic(err)
	}
}
