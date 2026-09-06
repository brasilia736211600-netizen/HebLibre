#!/usr/bin/env python3
"""Minimal local CONNECT proxy used only by the Android Runtime Smoke test."""
import select
import socket
import sys

HOST = "0.0.0.0"
BUFFER = 65536


def relay(a, b):
    while True:
        readable, _, _ = select.select([a, b], [], [], 30)
        if not readable:
            return
        for src in readable:
            data = src.recv(BUFFER)
            if not data:
                return
            dst = b if src is a else a
            dst.sendall(data)


def handle(client):
    client.settimeout(10)
    header = b""
    while b"\r\n\r\n" not in header and len(header) < 16384:
        chunk = client.recv(4096)
        if not chunk:
            return
        header += chunk
    first = header.split(b"\r\n", 1)[0].decode("ascii", "replace")
    parts = first.split()
    if len(parts) != 3 or parts[0].upper() != "CONNECT" or ":" not in parts[1]:
        client.sendall(b"HTTP/1.1 405 Method Not Allowed\r\nConnection: close\r\n\r\n")
        return
    host, port_text = parts[1].rsplit(":", 1)
    port = int(port_text)
    print(f"CONNECT {host}:{port}", flush=True)
    upstream = socket.create_connection((host, port), timeout=15)
    try:
        client.sendall(b"HTTP/1.1 200 Connection Established\r\n\r\n")
        relay(client, upstream)
    finally:
        upstream.close()


def main():
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 18080
    with socket.create_server((HOST, port), reuse_port=True) as server:
        print(f"LISTEN {HOST}:{port}", flush=True)
        while True:
            client, _ = server.accept()
            try:
                handle(client)
            except Exception as exc:
                print(f"ERROR {type(exc).__name__}: {exc}", flush=True)
            finally:
                client.close()


if __name__ == "__main__":
    main()
