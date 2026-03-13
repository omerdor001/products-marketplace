import { useState,useEffect } from "react";

const URL = "http://localhost:8080";

const emptyProduct = {
  name: "",
  description: "",
  image_url: "",
  price: "",
  marginPercentage: "",
  valueType: "STRING",
  value: "",
};

export default function ProductsPage() {
  const [search, setSearch] = useState("");
  const [sortBy, setSortBy] = useState("default");
  const [products, setProducts] = useState([]);
  const [editingProduct, setEditingProduct] = useState(null);
  const [addingProduct, setAddingProduct] = useState(false);
  const [newProduct, setNewProduct] = useState(emptyProduct);
  const [adminLoggedIn, setAdminLoggedIn] = useState(false);
  const [showAdminModal, setShowAdminModal] = useState(false);
  const [adminForm, setAdminForm] = useState({ username: "", password: "" });
  const [adminError, setAdminError] = useState("");
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [successProduct, setSuccessProduct] = useState(null);
  const [fetchError, setFetchError] = useState(null);
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    fetch(`${URL}/customer/products`)
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch products");
        return res.json();
      })
      .then((data) => {
        setProducts(data);
        setLoading(false);
      })
      .catch((err) => {
        setFetchError(err.message);
        setLoading(false);
      });
  }, []);

  const filtered = products
    .filter(
      (p) =>
        p.name.toLowerCase().includes(search.toLowerCase()) ||
        p.description.toLowerCase().includes(search.toLowerCase()),
    )
    .sort((a, b) =>
      sortBy === "price-asc"
        ? a.price - b.price
        : sortBy === "price-desc"
          ? b.price - a.price
          : 0,
    );

  const handleAdminLogin = async () => {
    try {
      const res = await fetch(`${URL}/admin/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: adminForm.username,
          password: adminForm.password,
        }),
      });
      const success = await res.json();
      if (!res.ok || !success) throw new Error();
      localStorage.setItem("username",adminForm.username);
      setAdminLoggedIn(true);
      setShowAdminModal(false);
      setAdminError("");
      setAdminForm({ username: "", password: "" });
    } catch {
      setAdminError("Invalid credentials.");
    }
  };

  const handleSaveEdit = async () => {
    const { id } = editingProduct;
    const username = localStorage.getItem("username");
    const calls = [];
    const original = products.find((p) => p.id === id);

    if (editingProduct.price !== original.price)
      calls.push(
        fetch(
          `${URL}/products/${id}/cost-price?${new URLSearchParams({ username, price: editingProduct.price })}`,
          { method: "PUT" },
        ),
      );

    if (editingProduct.marginPercentage !== original.marginPercentage)
      calls.push(
        fetch(
          `${URL}/products/${id}/margin?${new URLSearchParams({ username, marginPercentage: editingProduct.marginPercentage })}`,
          { method: "PUT" },
        ),
      );

    if (
      editingProduct.value !== original.value ||
      editingProduct.valueType !== original.valueType
    )
      calls.push(
        fetch(
          `${URL}/products/${id}/value?${new URLSearchParams({ username, valueType: editingProduct.valueType, value: editingProduct.value })}`,
          { method: "PUT" },
        ),
      );

    if (editingProduct.image_url !== original.image_url)
      calls.push(
        fetch(
          `${URL}/products/${id}/image-url?${new URLSearchParams({ username, image_url: editingProduct.image_url })}`,
          { method: "PUT" },
        ),
      );

    try {
      const results = await Promise.all(calls);
      const failed = results.find((r) => !r.ok);
      if (failed) throw new Error(await failed.text());

      setProducts((prev) =>
        prev.map((p) => (p.id === id ? { ...editingProduct } : p)),
      );
      setEditingProduct(null);
    } catch (err) {
      alert("Failed to update product: " + err.message);
    }
  };

  const handleAddProduct = async () => {
    const username = localStorage.getItem("username");
    if (!newProduct.name || !newProduct.price) return;
    try {
      const params = new URLSearchParams({
        username: username,
        name: newProduct.name,
        description: newProduct.description,
        image_url: newProduct.image_url,
        price: newProduct.price,
        marginPercentage: newProduct.marginPercentage,
        valueType: newProduct.valueType,
        value: newProduct.value,
      });
      const res = await fetch(`${URL}/products/coupon?${params}`, {
        method: "POST",
      });
      if (!res.ok) throw new Error(await res.text());
      const updated = await fetch(`${URL}/customer/products`);
      const data = await updated.json();
      setProducts(Array.isArray(data) ? data : data.content ?? data.products ?? []);
      setAddingProduct(false);
      setNewProduct(emptyProduct);
    } catch (err) {
      alert("Failed to add product: " + err.message);
    }
  };

  const handleDelete = async (id) => {
    const username = localStorage.getItem("username");
    try {
      const params = new URLSearchParams({ username: username });
      const res = await fetch(`${URL}/products/${id}?${params}`, {
        method: "DELETE",
      });
      if (!res.ok) throw new Error(await res.text());

      setProducts((prev) => prev.filter((p) => p.id !== id));
      setDeleteConfirm(null);
    } catch (err) {
      alert("Failed to delete product: " + err.message);
    }
  };

  const handlePurchase = async (product) => {
    try {
      const res = await fetch(`${URL}/customer/purchase/${product.id}`, {
        method: "GET",
      });
      if (!res.ok) throw new Error(await res.text());
      setSuccessProduct(product);
      setTimeout(() => setSuccessProduct(null), 3000);
      const updated = await fetch(`${URL}/customer/products`);
      const data = await updated.json();
      setProducts(Array.isArray(data) ? data : data.content ?? data.products ?? []);
    } catch (err) {
      alert("Purchase failed: " + err.message);
    }
  };

  console.log("Products from backend:", products.map(p => ({ name: p.name, image_url: p.image_url })));

  return (
    <div
      style={{
        fontFamily: "'DM Sans', sans-serif",
        background: "#f8f7f4",
        minHeight: "100vh",
        width: "100vw",
        overflowX: "hidden",
      }}
    >
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;700&family=DM+Sans:wght@300;400;500;600&display=swap');
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
        html, body { width: 100%; overflow-x: hidden; }
        .card { background: #fff; border-radius: 14px; overflow: hidden; border: 1px solid #ede9e0; display: flex; flex-direction: row; transition: box-shadow 0.25s, transform 0.25s; }
        .card:hover { transform: translateY(-2px); box-shadow: 0 10px 30px rgba(0,0,0,0.08); }
        .card img { width: 200px; height: 170px; object-fit: cover; flex-shrink: 0; transition: transform 0.35s; }
        .card:hover img { transform: scale(1.04); }
        .search-input { width: 100%; padding: 12px 20px 12px 44px; border: 1.5px solid #e5e1d8; border-radius: 10px; font-size: 14px; font-family: 'DM Sans', sans-serif; background: #fff; outline: none; transition: border 0.2s, box-shadow 0.2s; color: #1a1614; }
        .search-input:focus { border-color: #b8ad99; box-shadow: 0 0 0 3px rgba(184,173,153,0.18); }
        .sort-select { padding: 11px 16px; border: 1.5px solid #e5e1d8; border-radius: 10px; font-family: 'DM Sans', sans-serif; font-size: 13px; background: #fff; color: #555; cursor: pointer; outline: none; }
        .side-btn { width: 100%; padding: 10px 14px; border-radius: 10px; font-family: 'DM Sans', sans-serif; font-size: 13px; font-weight: 500; cursor: pointer; transition: all 0.2s; display: flex; align-items: center; gap: 8px; border: 1.5px solid transparent; }
        .modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 1000; display: flex; align-items: center; justify-content: center; backdrop-filter: blur(3px); }
        .modal { background: #fff; border-radius: 16px; padding: 36px; width: 460px; box-shadow: 0 24px 60px rgba(0,0,0,0.18); max-height: 90vh; overflow-y: auto; }
        .modal-input { width: 100%; padding: 11px 14px; border: 1.5px solid #e5e1d8; border-radius: 9px; font-family: 'DM Sans', sans-serif; font-size: 14px; outline: none; transition: border 0.2s; color: #1a1614; background: #fff; }
        .modal-input:focus { border-color: #b8ad99; }
        .img-wrap { overflow: hidden; flex-shrink: 0; }
        .icon-btn { background: none; border: none; cursor: pointer; border-radius: 7px; padding: 6px 8px; transition: background 0.15s; font-size: 15px; }
        .icon-btn:hover { background: #f3f0eb; }
        .icon-btn.red:hover { background: #fee2e2; }
        .primary-btn { padding: 11px 22px; background: #1a1614; color: #fff; border: none; border-radius: 9px; font-family: 'DM Sans', sans-serif; font-weight: 600; font-size: 14px; cursor: pointer; transition: background 0.2s; }
        .primary-btn:hover { background: #333; }
        .ghost-btn { padding: 11px 22px; background: #f8f7f4; color: #555; border: 1.5px solid #e5e1d8; border-radius: 9px; font-family: 'DM Sans', sans-serif; font-weight: 500; font-size: 14px; cursor: pointer; }
        .buy-btn { padding: 9px 24px; background: #1a1614; color: #fff; border: none; border-radius: 9px; font-family: 'DM Sans', sans-serif; font-weight: 600; font-size: 13px; cursor: pointer; transition: all 0.2s; letter-spacing: 0.2px; }
        .buy-btn:hover { background: #3a3330; transform: scale(1.03); }
        .field-label { font-size: 11px; font-weight: 600; color: #888; text-transform: uppercase; letter-spacing: 0.6px; display: block; margin-bottom: 5px; }
        @keyframes fadeSlideIn { from { opacity: 0; transform: scale(0.88) translateY(12px); } to { opacity: 1; transform: scale(1) translateY(0); } }
        @keyframes popIn { 0% { transform: scale(0.4); opacity: 0; } 65% { transform: scale(1.2); } 100% { transform: scale(1); opacity: 1; } }
        .success-modal { animation: fadeSlideIn 0.3s ease forwards; }
        .success-icon { animation: popIn 0.45s ease 0.15s both; }
      `}</style>

      <div
        style={{
          background: "#fff",
          borderBottom: "1px solid #ede9e0",
          position: "sticky",
          top: 0,
          zIndex: 50,
          width: "100%",
        }}
      >
        <div
          style={{
            width: "100%",
            padding: "15px 40px",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: "20px",
          }}
        >
          <h1
            style={{
              fontFamily: "'Playfair Display', serif",
              fontSize: "35px",
              fontWeight: 700,
              color: "#1a1614",
              whiteSpace: "nowrap",
            }}
          >
            Market
          </h1>
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "12px",
              flex: 1,
              maxWidth: "700px",
            }}
          >
            <div style={{ position: "relative", flex: 1 }}>
              <span
                style={{
                  position: "absolute",
                  left: "14px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  color: "#aaa",
                  fontSize: "15px",
                }}
              >
                🔍
              </span>
              <input
                className="search-input"
                placeholder="Search products..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
            <select
              className="sort-select"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
            >
              <option value="default">Featured</option>
              <option value="price-asc">Price: Low → High</option>
              <option value="price-desc">Price: High → Low</option>
            </select>
          </div>
          <span></span>
        </div>
      </div>

      <div
        style={{
          width: "100%",
          padding: "36px 40px",
          display: "flex",
          gap: "32px",
        }}
      >
        <div
          style={{
            width: "210px",
            flexShrink: 0,
            display: "flex",
            flexDirection: "column",
            gap: "10px",
          }}
        >
          <p
            style={{
              fontSize: "11px",
              fontWeight: 600,
              color: "#bbb",
              textTransform: "uppercase",
              letterSpacing: "1px",
              marginBottom: "4px",
            }}
          >
            Management
          </p>
          {!adminLoggedIn ? (
            <button
              className="side-btn"
              onClick={() => setShowAdminModal(true)}
              style={{ background: "#1a1614", color: "#fff" }}
            >
              🔐 Admin Login
            </button>
          ) : (
            <button
              className="side-btn"
              onClick={() => {
                setAdminLoggedIn(false);
                setEditMode(false);
              }}
              style={{
                background: "#fee2e2",
                color: "#dc2626",
                borderColor: "#fca5a5",
              }}
            >
              🚪 Logout
            </button>
          )}
          {adminLoggedIn && (
            <>
              <button
                className="side-btn"
                onClick={() => setEditMode((e) => !e)}
                style={{
                  background: editMode ? "#fffbeb" : "#f8f7f4",
                  color: editMode ? "#d97706" : "#555",
                  borderColor: editMode ? "#fcd34d" : "#e5e1d8",
                }}
              >
                ✏️ {editMode ? "Exit Edit Mode" : "Edit Mode"}
              </button>
              <button
                className="side-btn"
                onClick={() => setAddingProduct(true)}
                style={{
                  background: "#f0fdf4",
                  color: "#16a34a",
                  borderColor: "#bbf7d0",
                }}
              >
                ➕ Add Product
              </button>
              <div
                style={{
                  marginTop: "6px",
                  padding: "10px 12px",
                  background: "#f0fdf4",
                  borderRadius: "9px",
                  border: "1px solid #bbf7d0",
                }}
              >
                <p
                  style={{
                    fontSize: "12px",
                    color: "#16a34a",
                    fontWeight: 500,
                  }}
                >
                  ✓ Admin active
                </p>
              </div>
              {editMode && (
                <div
                  style={{
                    padding: "10px 12px ",
                    background: "#fffbeb",
                    borderRadius: "9px",
                    border: "1px solid #fde68a",
                  }}
                >
                  <p
                    style={{
                      fontSize: "12px",
                      color: "#92400e",
                      lineHeight: 1.5,
                    }}
                  >
                    Hover a card to edit or delete
                  </p>
                </div>
              )}
            </>
          )}
        </div>

        <div style={{ flex: 1, minWidth: 0 }}>
          <h2
            style={{
              fontFamily: "'Playfair Display', serif",
              fontSize: "28px",
              fontWeight: 700,
              color: "#1a1614",
              marginBottom: "24px",
            }}
          >
            All Products
          </h2>
          {filtered.length === 0 ? (
            <div
              style={{ textAlign: "center", padding: "100px 0", color: "#bbb" }}
            >
              <div style={{ fontSize: "48px", marginBottom: "14px" }}>🔍</div>
              <p style={{ fontSize: "17px", fontWeight: 500 }}>
                No products found
              </p>
            </div>
          ) : (
            <div
              style={{ display: "flex", flexDirection: "column", gap: "16px" }}
            >
              {filtered.map((product) => (
                <div
                  key={product.id}
                  className="card"
                  style={{ position: "relative" }}
                >
                  <div className="img-wrap">
                   <img
  src={product.image_url}
  alt={product.name}
  onLoad={() => console.log(`✅ Loaded: ${product.name} →`, product.image_url)}
  onError={() => console.log(`❌ Failed: ${product.name} →`, product.image_url)}
/>
                  </div>
                  <div
                    style={{
                      padding: "20px 100px 20px 30px",
                      flex: 1,
                      display: "flex",
                      flexDirection: "column",
                      justifyContent: "center",
                      gap: "8px",
                    }}
                  >
                    <div
                      style={{
                        display: "flex",
                        alignItems: "flex-start",
                        justifyContent: "space-between",
                        gap: "12px",
                      }}
                    >
                      <h3
                        style={{
                          fontFamily: "'Playfair Display', serif",
                          fontSize: "18px",
                          fontWeight: 600,
                          color: "#1a1614",
                        }}
                      >
                        {product.name}
                      </h3>
                      <span
                        style={{
                          fontFamily: "'Playfair Display', serif",
                          fontSize: "22px",
                          fontWeight: 700,
                          color: "#1a1614",
                          whiteSpace: "nowrap",
                        }}
                      >
                        ${product.price}
                      </span>
                    </div>
                    <p
                      style={{
                        fontSize: "14px",
                        color: "#888",
                        lineHeight: 1.6,
                      }}
                    >
                      {product.description}
                    </p>
                    {!adminLoggedIn && (
                      <div style={{ marginTop: "6px" }}>
                        <button
                          className="buy-btn"
                          onClick={() => handlePurchase(product)}
                        >
                          Buy Now
                        </button>
                      </div>
                    )}
                  </div>
                  {editMode && (
                    <div
                      style={{
                        position: "absolute",
                        top: "10px",
                        right: "10px",
                        display: "flex",
                        gap: "6px",
                      }}
                    >
                      <button
                        className="icon-btn"
                        onClick={() => setEditingProduct({ ...product })}
                        style={{
                          background: "#fff",
                          border: "1px solid #e5e1d8",
                          fontSize: "14px",
                        }}
                      >
                        ✏️
                      </button>
                      <button
                        className="icon-btn red"
                        onClick={() => setDeleteConfirm(product.id)}
                        style={{
                          background: "#fff",
                          border: "1px solid #fca5a5",
                          fontSize: "14px",
                        }}
                      >
                        🗑️
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {showAdminModal && (
        <div className="modal-overlay" onClick={() => setShowAdminModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2
              style={{
                fontFamily: "'Playfair Display', serif",
                fontSize: "22px",
                fontWeight: 700,
                color: "#1a1614",
                marginBottom: "6px",
              }}
            >
              Admin Login
            </h2>
            <p
              style={{ fontSize: "13px", color: "#aaa", marginBottom: "24px" }}
            >
              Sign in to manage products
            </p>
            <div style={{ marginBottom: "12px" }}>
              <input
                className="modal-input"
                placeholder="Username"
                value={adminForm.username}
                onChange={(e) =>
                  setAdminForm((p) => ({ ...p, username: e.target.value }))
                }
              />
            </div>
            <div style={{ marginBottom: "12px" }}>
              <input
                className="modal-input"
                placeholder="Password"
                type="password"
                value={adminForm.password}
                onChange={(e) =>
                  setAdminForm((p) => ({ ...p, password: e.target.value }))
                }
                onKeyDown={(e) => e.key === "Enter" && handleAdminLogin()}
              />
            </div>
            {adminError && (
              <p
                style={{
                  color: "#dc2626",
                  fontSize: "13px",
                  marginBottom: "12px",
                }}
              >
                {adminError}
              </p>
            )}
            <div style={{ display: "flex", gap: "10px", marginTop: "8px" }}>
              <button
                className="primary-btn"
                style={{ flex: 1 }}
                onClick={handleAdminLogin}
              >
                Login
              </button>
              <button
                className="ghost-btn"
                style={{ flex: 1 }}
                onClick={() => setShowAdminModal(false)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {editingProduct && (
        <div className="modal-overlay" onClick={() => setEditingProduct(null)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2
              style={{
                fontFamily: "'Playfair Display', serif",
                fontSize: "22px",
                fontWeight: 700,
                color: "#1a1614",
                marginBottom: "22px",
              }}
            >
              Edit Product
            </h2>
            {[
              ["price", "Cost Price", "number"],
              ["marginPercentage", "Margin %", "number"],
              ["valueType", "Value Type", "select"],
              ["value", "Value", "text"],
              ["image_url", "Image URL", "text"],
            ].map(([field, label, type]) => (
              <div key={field} style={{ marginBottom: "14px" }}>
                <label className="field-label">{label}</label>
                {type === "select" ? (
                  <select
                    className="modal-input"
                    value={editingProduct[field]}
                    onChange={(e) =>
                      setEditingProduct((p) => ({
                        ...p,
                        [field]: e.target.value,
                      }))
                    }
                  >
                    <option value="STRING">STRING</option>
                    <option value="IMAGE">IMAGE</option>
                  </select>
                ) : (
                  <input
                    className="modal-input"
                    type={type}
                    value={editingProduct[field]}
                    onChange={(e) =>
                      setEditingProduct((p) => ({
                        ...p,
                        [field]: e.target.value,
                      }))
                    }
                  />
                )}
              </div>
            ))}
            <div style={{ display: "flex", gap: "10px", marginTop: "8px" }}>
              <button
                className="primary-btn"
                style={{ flex: 1 }}
                onClick={handleSaveEdit}
              >
                Save Changes
              </button>
              <button
                className="ghost-btn"
                style={{ flex: 1 }}
                onClick={() => setEditingProduct(null)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {addingProduct && (
        <div className="modal-overlay" onClick={() => setAddingProduct(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2
              style={{
                fontFamily: "'Playfair Display', serif",
                fontSize: "22px",
                fontWeight: 700,
                color: "#1a1614",
                marginBottom: "22px",
              }}
            >
              Add New Product
            </h2>
            {[
              ["name", "Name", "text"],
              ["description", "Description", "textarea"],
              ["image_url", "Image URL", "text"],
              ["price", "Cost Price", "number"],
              ["marginPercentage", "Margin %", "number"],
              ["valueType", "Value Type", "select"],
              ["value", "Value", "text"],
            ].map(([field, label, type]) => (
              <div key={field} style={{ marginBottom: "14px" }}>
                <label className="field-label">{label}</label>
                {type === "textarea" ? (
                  <textarea
                    className="modal-input"
                    rows={3}
                    style={{ resize: "vertical" }}
                    value={newProduct[field]}
                    onChange={(e) =>
                      setNewProduct((p) => ({ ...p, [field]: e.target.value }))
                    }
                  />
                ) : type === "select" ? (
                  <select
                    className="modal-input"
                    value={newProduct[field]}
                    onChange={(e) =>
                      setNewProduct((p) => ({ ...p, [field]: e.target.value }))
                    }
                  >
                    <option value="STRING">STRING</option>
                    <option value="IMAGE">IMAGE</option>
                  </select>
                ) : (
                  <input
                    className="modal-input"
                    type={type}
                    value={newProduct[field]}
                    onChange={(e) =>
                      setNewProduct((p) => ({ ...p, [field]: e.target.value }))
                    }
                  />
                )}
              </div>
            ))}
            <div style={{ display: "flex", gap: "10px", marginTop: "8px" }}>
              <button
                className="primary-btn"
                style={{ flex: 1 }}
                onClick={handleAddProduct}
              >
                Add Product
              </button>
              <button
                className="ghost-btn"
                style={{ flex: 1 }}
                onClick={() => {
                  setAddingProduct(false);
                  setNewProduct(emptyProduct);
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {deleteConfirm && (
        <div className="modal-overlay" onClick={() => setDeleteConfirm(null)}>
          <div
            className="modal"
            style={{ width: "380px" }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{ fontSize: "36px", marginBottom: "14px" }}>🗑️</div>
            <h2
              style={{
                fontFamily: "'Playfair Display', serif",
                fontSize: "20px",
                fontWeight: 700,
                color: "#1a1614",
                marginBottom: "8px",
              }}
            >
              Delete Product?
            </h2>
            <p
              style={{ fontSize: "14px", color: "#888", marginBottom: "24px" }}
            >
              This action cannot be undone.
            </p>
            <div style={{ display: "flex", gap: "10px" }}>
              <button
                onClick={() => handleDelete(deleteConfirm)}
                style={{
                  flex: 1,
                  padding: "11px",
                  background: "#dc2626",
                  color: "#fff",
                  border: "none",
                  borderRadius: "9px",
                  fontFamily: "'DM Sans', sans-serif",
                  fontWeight: 600,
                  fontSize: "14px",
                  cursor: "pointer",
                }}
              >
                Delete
              </button>
              <button
                className="ghost-btn"
                style={{ flex: 1 }}
                onClick={() => setDeleteConfirm(null)}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {successProduct && (
        <div className="modal-overlay" onClick={() => setSuccessProduct(null)}>
          <div
            className="modal success-modal"
            style={{
              width: "360px",
              textAlign: "center",
              padding: "48px 36px",
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div
              className="success-icon"
              style={{ fontSize: "64px", marginBottom: "20px", lineHeight: 1 }}
            >
              🎉
            </div>
            <h2
              style={{
                fontFamily: "'Playfair Display', serif",
                fontSize: "24px",
                fontWeight: 700,
                color: "#1a1614",
                marginBottom: "10px",
              }}
            >
              Purchase Successful!
            </h2>
            <p
              style={{
                fontSize: "14px",
                color: "#888",
                lineHeight: 1.6,
                marginBottom: "6px",
              }}
            >
              <strong style={{ color: "#1a1614" }}>
                {successProduct.name}
              </strong>{" "}
              is on its way to you.
            </p>
            <p style={{ fontSize: "13px", color: "#bbb" }}>
              Thank you for your order 🙏
            </p>
            <div
              style={{
                marginTop: "28px",
                height: "3px",
                background: "#f0ece4",
                borderRadius: "10px",
                overflow: "hidden",
              }}
            >
              <div
                style={{
                  height: "100%",
                  background: "#c4b89a",
                  borderRadius: "10px",
                  animation: "shrink 3s linear forwards",
                }}
              />
            </div>
            <style>{`@keyframes shrink { from { width: 100%; } to { width: 0%; } }`}</style>
          </div>
        </div>
      )}
    </div>
  );
}
