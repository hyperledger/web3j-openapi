for f in *.sol; do
	docker run -v $PWD:/sources ethereum/solc:0.5.8 --abi --bin /sources/$f -o /sources/build/ openzeppelin-solidity=/sources/openzeppelin --overwrite
done
