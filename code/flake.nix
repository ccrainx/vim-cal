{
  description = "Nix flake for Java calculator";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }: 
    let
      pkgs = nixpkgs.legacyPackages.x86_64-linux;
    in
    {
      packages.x86_64-linux.calc-app = pkgs.stdenv.mkDerivation {
        pname = "calc-app";
        version = "1.0";
        src = ./.; # folder with main.jar
        buildInputs = [ pkgs.openjdk ];

        installPhase = ''
          mkdir -p $out/bin
          cp main.jar $out/bin/
          echo '#!/usr/bin/env bash' > $out/bin/calc
          echo 'java -jar $PWD/main.jar' >> $out/bin/calc
          chmod +x $out/bin/calc
        '';
      };
    };
}
