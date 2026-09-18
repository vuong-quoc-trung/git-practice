use std::thread;

fn main() {
    let text = String::from(
        "Rust nhanh, an toan, va phu hop de viet phan mem da luong.",
    );

    // Chia du lieu thanh cac phan de xu ly song song.
    let words: Vec<&str> = text.split_whitespace().collect();
    let mid = words.len() / 2;
    let (left, right) = words.split_at(mid);

    // `thread::scope` cho phep thread muon du lieu tam thoi mot cach an toan.
    // Rust se khong cho phep truy cap du lieu sai hoac gay data race.
    let total = thread::scope(|scope| {
        let first_worker = scope.spawn(|| left.len());
        let second_worker = scope.spawn(|| right.len());

        first_worker.join().expect("worker 1 bi loi")
            + second_worker.join().expect("worker 2 bi loi")
    });

    println!("Van ban: {text}");
    println!("So tu: {total}");
    println!("Hai worker da xu ly song song ma khong can lock.");
}